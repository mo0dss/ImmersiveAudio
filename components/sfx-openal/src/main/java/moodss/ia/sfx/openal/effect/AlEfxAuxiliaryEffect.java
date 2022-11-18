package moodss.ia.sfx.openal.effect;

import moodss.ia.sfx.api.effects.AuxiliaryEffect;
import moodss.ia.sfx.api.effects.types.AuxiliaryEffectProperty;
import moodss.ia.sfx.openal.AlObject;
import moodss.ia.sfx.openal.EfxEnum;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.EXTEfx;

public class AlEfxAuxiliaryEffect extends AlObject implements AuxiliaryEffect {

    public <T> AlEfxAuxiliaryEffect(int handle, boolean sendAuto) {
        this.setHandle(handle);

        if(sendAuto) {
            EXTEfx.alAuxiliaryEffectSloti(handle, EfxEnum.from(AuxiliaryEffectProperty.SEND_AUTO), AL10.AL_TRUE);
        }
    }
}
