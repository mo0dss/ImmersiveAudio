package moodss.ia.openal;

import moodss.ia.sfx.api.device.AudioDevice;
import moodss.ia.sfx.api.effect.AuxiliaryEffect;
import moodss.ia.sfx.api.types.ALCToken;

public class AuxiliaryEffectManager {

    /**
     * Max auxiliary effects the attached device can play
     */
    private final int maxAuxiliarySends;

    /**
     * Auxiliary effects
     */
    private final AuxiliaryEffect[] effects;

    private final AudioDevice device;

    public AuxiliaryEffectManager(AudioDevice device, boolean sendAuto) {
        this.device = device;

        int maxAuxiliarySends = this.maxAuxiliarySends = device.getToken(ALCToken.MAX_AUXILIARY_SENDS);
        this.effects = new AuxiliaryEffect[maxAuxiliarySends];

        for(int idx = 0; idx < this.effects.length; ++idx) {
            this.effects[idx] = device.createAuxiliaryEffect(sendAuto);
        }
    }

    public int getMaxAuxiliaries() {
        return this.maxAuxiliarySends;
    }

    public AuxiliaryEffect getAuxiliaryEffect(int idx) {
        return this.effects[Math.floorMod(idx, this.effects.length)];
    }

    public void destroy() {
        for(AuxiliaryEffect effect : this.effects) {
            this.device.deleteAuxiliaryEffect(effect);
        }
    }
}
