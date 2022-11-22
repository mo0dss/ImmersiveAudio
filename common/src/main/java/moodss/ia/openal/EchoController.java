package moodss.ia.openal;

import moodss.ia.sfx.api.device.AudioDevice;
import moodss.ia.sfx.api.device.AudioDeviceContext;
import moodss.ia.sfx.api.effects.Effect;
import moodss.ia.sfx.api.effects.types.EchoProperties;
import moodss.ia.sfx.api.effects.types.EffectType;
import moodss.ia.sfx.openal.source.AlSource;
import moodss.plummet.math.MathUtils;

public class EchoController {
    /**
     * Echo effects
     */
    private Effect[] effects;

    public EchoController() {
        //NO-OP; for now?
    }

    public void init(AudioDevice device, AuxiliaryEffectManager auxiliaryEffectManager) {
        int maxAuxiliaries = auxiliaryEffectManager.getMaxAuxiliaries();
        this.effects = new Effect[maxAuxiliaries];

        for(int idx = 0; idx < maxAuxiliaries; ++idx) {
            this.effects[idx] = device.createEffect(EffectType.ECHO);
        }

        device.run(context -> {
            for(int idx = 0; idx < maxAuxiliaries; ++idx) {
                float unit = (float) idx / maxAuxiliaries;
                Effect effect = this.getEffect(idx);
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
        context.setEcho(effect, EchoProperties.DELAY, unit * 0.01F);
        context.setEcho(effect, EchoProperties.DELAY_LR, Math.max(0.95F - 0.01F * unit, 0.1F));
        context.setEcho(effect, EchoProperties.DAMPING, MathUtils.lerp(0.618F, 1.0F - unit, 1.0F));
        context.setEcho(effect, EchoProperties.FEEDBACK, unit * 0.5F + 0.5F);
        context.setEcho(effect, EchoProperties.SPREAD, MathUtils.pow(unit, 0.5F) + 0.618F);
    }

    public void destroy(AudioDevice device) {
        for(Effect effect : this.effects) {
            device.deleteEffect(effect);
        }
    }
}
