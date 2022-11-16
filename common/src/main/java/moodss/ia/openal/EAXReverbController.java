package moodss.ia.openal;

import moodss.ia.sfx.api.device.AudioDevice;
import moodss.ia.sfx.api.device.AudioDeviceContext;
import moodss.ia.sfx.api.effect.Effect;
import moodss.ia.sfx.api.filter.Filter;
import moodss.ia.sfx.api.types.EAXReverbProperties;
import moodss.ia.sfx.api.types.EffectType;
import moodss.ia.sfx.api.types.FilterType;
import moodss.ia.sfx.openal.source.AlSource;
import moodss.plummet.math.MathUtils;

public class EAXReverbController {
    /**
     * EAXReverb effects
     */
    private Effect[] effects;

    /**
     * EAXReverb filters
     */
    private Filter[] filters;

    public EAXReverbController() {
        //NO-OP; for now?
    }

    public void init(AudioDevice device, AuxiliaryEffectManager auxiliaryEffectManager) {
        int maxAuxiliarySends = auxiliaryEffectManager.getMaxAuxiliaries();
        this.effects = new Effect[maxAuxiliarySends];
        this.filters = new Filter[maxAuxiliarySends];

        for(int idx = 0; idx < maxAuxiliarySends; ++idx) {
            this.effects[idx] = device.createEffect(EffectType.EAXREVERB);
            this.filters[idx] = device.createFilter(FilterType.LOWPASS);
        }

        device.run(context -> {
            for(int idx = 0; idx < maxAuxiliarySends; ++idx) {
                float unit = (float) idx / maxAuxiliarySends;
                Effect effect = this.getEffect(idx);
                setEffectProperties(context, effect, unit);
                context.bindAuxiliaryEffect(auxiliaryEffectManager.getAuxiliaryEffect(idx), effect);
            }
        });
    }


    public void applyToSource(AudioDeviceContext context, AuxiliaryEffectManager auxiliaryEffectManager, AlSource source) {
        for(int idx = 0; idx < this.effects.length; idx++) {
            context.bindSourceAuxiliarySendFilter(source, auxiliaryEffectManager.getAuxiliaryEffect(idx), this.filters[idx], idx);
        }
    }

    public Effect getEffect(int idx) {
        return this.effects[Math.floorMod(idx, this.effects.length)];
    }

    public Filter getFilter(int idx) {
        return this.filters[Math.floorMod(idx, this.filters.length)];
    }

    protected static void setEffectProperties(AudioDeviceContext context, Effect effect, float unit) {
        context.setEAXReverb(effect, EAXReverbProperties.DECAY_TIME, MathUtils.clamp(unit * 4.142F, 0.1F, 20F));
        context.setEAXReverb(effect, EAXReverbProperties.DENSITY, unit * 0.5F + 0.5F);
        context.setEAXReverb(effect, EAXReverbProperties.DIFFUSION, MathUtils.lerp(0.618F, 1.0F - unit, 1.0F));
        context.setEAXReverb(effect, EAXReverbProperties.DECAY_RATIO_HF, Math.max(0.95F - 0.3F * unit, 0.1F));
        context.setEAXReverb(effect, EAXReverbProperties.REFLECTIONS_GAIN, Math.max((float) Math.pow(1.0F - unit, 0.5F) + 0.618F, 0.1F));
        context.setEAXReverb(effect, EAXReverbProperties.REFLECTIONS_DELAY, unit * 0.01F);
        context.setEAXReverb(effect, EAXReverbProperties.LATE_GAIN, (float)(Math.pow(unit, 0.5) + 0.618F));
        context.setEAXReverb(effect, EAXReverbProperties.LATE_DELAY, unit * 0.01F);
    }

    public void destroy(AudioDevice device) {
        for(int idx = 0; idx < this.effects.length; idx++) {
            device.deleteEffect(this.effects[idx]);
            device.deleteFilter(this.filters[idx]);
        }
    }
}
