package moodss.ia.openal;

import moodss.ia.sfx.api.device.AudioDevice;
import moodss.ia.sfx.api.effect.AuxiliaryEffect;
import moodss.ia.sfx.api.types.ALCToken;

public class AuxiliaryEffectManager {

    private final int maxAuxiliaries;

    private final AuxiliaryEffect[] effects;

    private final AudioDevice device;

    public AuxiliaryEffectManager(AudioDevice device, boolean sendAuto) {
        this.device = device;
        this.maxAuxiliaries = device.getToken(ALCToken.MAX_AUXILIARY_SENDS);
        this.effects = new AuxiliaryEffect[this.maxAuxiliaries];

        for(int idx = 0; idx < this.effects.length; idx++) {
            this.effects[idx] = device.createAuxiliaryEffect(sendAuto);
        }
    }

    public int getMaxAuxiliaries() {
        return this.maxAuxiliaries;
    }

    public AuxiliaryEffect getAuxiliaryEffect(int idx) {
        return this.effects[Math.floorMod(idx, this.effects.length)];
    }

    public void destroy() {
        for (AuxiliaryEffect effect : this.effects) {
            device.deleteAuxiliaryEffect(effect);
        }
    }
}
