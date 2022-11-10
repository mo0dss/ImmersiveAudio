package moodss.ia.openal;

import moodss.ia.sfx.api.AudioException;
import moodss.ia.sfx.api.device.AudioDevice;
import moodss.ia.sfx.api.device.AudioDeviceContext;
import moodss.ia.sfx.api.device.AudioDeviceGate;
import moodss.ia.sfx.api.effect.Effect;
import moodss.ia.sfx.api.types.EchoProperties;
import moodss.ia.sfx.api.types.EffectType;
import moodss.ia.sfx.openal.source.AlSource;
import moodss.plummet.math.MathUtils;

public class EchoController {

    private final AudioDevice device;
    private final Effect[] effects;

    public EchoController(AudioDevice device, AuxiliaryEffectManager auxiliaryEffectManager) {
        this.device = device;
        int maxAuxiliaries = auxiliaryEffectManager.getMaxAuxiliaries();

        this.effects = new Effect[maxAuxiliaries];

        for(int idx = 0; idx < maxAuxiliaries; idx++) {
            this.effects[idx] = device.createEffect(EffectType.ECHO);
        }

        EchoController controller = EchoController.this;

        device.run(context -> {
            for(int idx = 0; idx < maxAuxiliaries; idx++) {
                float unit = (float) idx / maxAuxiliaries;
                var effect = controller.getEffect(idx);
                setEffectProperties(context, effect, unit);
                context.bindAuxiliaryEffect(auxiliaryEffectManager.getAuxiliaryEffect(idx), effect);
            }
        });
    }

    public void apply(AlSource source) {

    }

    public Effect getEffect(int idx) {
        return this.effects[Math.floorMod(idx, this.effects.length)];
    }

    protected static void setEffectProperties(AudioDeviceContext context, Effect effect, float unit) {
        context.setEcho(effect, EchoProperties.DELAY, (unit * 0.01F));
        context.setEcho(effect, EchoProperties.DELAY_LR, Math.max(0.95F - (0.01F * unit), 0.1F));
        context.setEcho(effect, EchoProperties.DAMPING, MathUtils.lerp(0.618F, 1F - unit, 1F));
        context.setEcho(effect, EchoProperties.FEEDBACK, (unit * 0.5F + 0.5F));
        context.setEcho(effect, EchoProperties.SPREAD, (float) (Math.pow(unit, 0.5F) + 0.618F));
    }

    public void destroy() {
        for(int idx = 0; idx < this.effects.length; idx++) {
            this.device.deleteEffect(this.effects[idx]);
        }
    }
}
