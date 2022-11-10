package moodss.ia.openal;

import moodss.ia.ray.CollisionObelisk;
import moodss.ia.ray.RaytracedAudio;
import moodss.ia.ray.v2.BiDirectionalPathStrengthManager;
import moodss.ia.sfx.api.AudioException;
import moodss.ia.sfx.api.device.AudioDevice;
import moodss.ia.sfx.api.device.AudioDeviceContext;
import moodss.ia.sfx.api.device.AudioDeviceGate;
import moodss.ia.sfx.api.effect.Effect;
import moodss.ia.sfx.api.filter.Filter;
import moodss.ia.sfx.api.types.EAXReverbProperties;
import moodss.ia.sfx.api.types.EffectType;
import moodss.ia.sfx.api.types.FilterType;
import moodss.ia.sfx.openal.source.AlSource;
import moodss.ia.user.ImmersiveAudioConfig;
import moodss.plummet.math.MathUtils;
import moodss.plummet.math.vec.Vector3;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EAXReverbController {

    private final AudioDevice device;

    private final Effect[] effects;
    private final Filter[] filters;

    private final AuxiliaryEffectManager auxiliaryEffectManager;

    private final RaytracedAudio audio;

    public EAXReverbController(AudioDevice device, AuxiliaryEffectManager auxiliaryEffectManager, ImmersiveAudioConfig.Raytracing raytracing) {
        this.device = device;
        this.auxiliaryEffectManager = auxiliaryEffectManager;

        int maxAuxiliaries = auxiliaryEffectManager.getMaxAuxiliaries();

        this.effects = new Effect[maxAuxiliaries];
        this.filters = new Filter[maxAuxiliaries];

        for(int idx = 0; idx < maxAuxiliaries; idx++) {
            this.effects[idx] = device.createEffect(EffectType.EAXREVERB);
            this.filters[idx] = device.createFilter(FilterType.LOWPASS);
        }

        EAXReverbController controller = EAXReverbController.this;

        device.run(context -> {
            for(int idx = 0; idx < maxAuxiliaries; idx++){
                float unit = (float) idx / maxAuxiliaries;
                var effect = controller.getEffect(idx);
                setEffectProperties(context, effect, unit);
                context.bindAuxiliaryEffect(auxiliaryEffectManager.getAuxiliaryEffect(idx), effect);
            }
        });

        this.audio = new RaytracedAudio(raytracing);
    }

    public <T> Vector3 computeOriginFor(
            AlSource source,
            Vector3 origin, Vector3 listener,
            BiFunction<Vector3, Vector3, CollisionObelisk> traceFunc,
            Function<T, Float> reflectivityFunc,
            float maxDistance, Executor executor) {
        AuxiliaryEffectManager auxiliaryEffectManager = this.auxiliaryEffectManager;


        int maxRayCount = this.audio.getMaxRayCount();
        float[] bounceReflectivityRatio = new float[this.audio.getMaxRayCount()];
        float[] gain = new float[this.auxiliaryEffectManager.getMaxAuxiliaries()];

        CompletableFuture<Vector3> computedOrigin = this.audio.computeOrigin(origin, listener, traceFunc, reflectivityFunc, bounceReflectivityRatio, gain, maxDistance, executor);
        BiDirectionalPathStrengthManager reflectedAudio = this.audio.getPathtracer();

        for (int i = 0; i < bounceReflectivityRatio.length; i++) {
            bounceReflectivityRatio[i] = bounceReflectivityRatio[i] / maxRayCount;
        }

        float sharedAirspace = reflectedAudio.getCurrentEntryIdx() * 64F * (1F / maxRayCount);
        Filter[] filters = this.filters;

        this.device.run(new AudioDeviceGate() {
            @Override
            public void apply(AudioDeviceContext context) throws AudioException {
                for(int idx = 0; idx < auxiliaryEffectManager.getMaxAuxiliaries(); idx++) {
                    float sharedAirspaceWeight = MathUtils.clamp(sharedAirspace / (idx * 20F - 10F), 0F, 1F);

                    float sendGain = gain[idx];
                    float sendCutoff = (float) (Math.exp(-3F * 1F) * (1F - sharedAirspaceWeight) + sharedAirspaceWeight);

                    sendGain *= bounceReflectivityRatio[idx];
                    sendGain = MathUtils.clamp(sendGain, 0F, 1F);

                    sendGain *= Math.pow(sendCutoff, 0.1F);

                    context.setGain(filters[idx], sendGain);
                    context.setGainHF(filters[idx], sendCutoff);

                    context.bindSourceAuxiliarySendFilter(source, auxiliaryEffectManager.getAuxiliaryEffect(idx), filters[idx], idx);
                }
            }
        });

        return computedOrigin.join();
    }

    public RaytracedAudio getAudio() {
        return audio;
    }

    public Effect getEffect(int idx) {
        return this.effects[Math.floorMod(idx, this.effects.length)];
    }

    public Filter getFilter(int idx) {
        return this.filters[Math.floorMod(idx, this.filters.length)];
    }

    protected static void setEffectProperties(AudioDeviceContext context, Effect effect, float unit) {
        context.setEAXReverb(effect, EAXReverbProperties.DECAY_TIME, Math.max(unit * 4.142F, 0.1F));
        context.setEAXReverb(effect, EAXReverbProperties.DENSITY, (unit * 0.5F + 0.5F));
        context.setEAXReverb(effect, EAXReverbProperties.DIFFUSION, MathUtils.lerp(0.618F, 1F - unit, 1F));
        context.setEAXReverb(effect, EAXReverbProperties.DECAY_RATIO_HF, Math.max(0.95F - (0.3F * unit), 0.1F));
        context.setEAXReverb(effect, EAXReverbProperties.REFLECTIONS_GAIN, Math.max((float) Math.pow(1F - unit, 0.5F) + 0.618F, 0.1F));
        context.setEAXReverb(effect, EAXReverbProperties.REFLECTIONS_DELAY, (unit * 0.01F));
        context.setEAXReverb(effect, EAXReverbProperties.LATE_GAIN, (float) (Math.pow(unit, 0.5F) + 0.618F));
        context.setEAXReverb(effect, EAXReverbProperties.LATE_DELAY, (unit * 0.01F));
    }

    public void destroy() {
        for(int idx = 0; idx < this.effects.length; idx++) {
            this.device.deleteEffect(this.effects[idx]);
            this.device.deleteFilter(this.filters[idx]);
        }
    }
}
