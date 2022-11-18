package moodss.ia.sfx.openal.effect;

import moodss.ia.sfx.api.effects.Effect;
import moodss.ia.sfx.api.effects.types.EffectType;
import moodss.ia.sfx.openal.AlObject;
import moodss.ia.sfx.openal.EfxEnum;
import org.lwjgl.openal.EXTEfx;

public class AlEfxEffect extends AlObject implements Effect {

    private final EffectType type;

    public AlEfxEffect(EffectType type) {
        var handle = EXTEfx.alGenEffects();
        this.setHandle(handle);
        this.type = type;

        EXTEfx.alEffecti(handle, EXTEfx.AL_EFFECT_TYPE, EfxEnum.from(type));
    }

    @Override
    public EffectType type() {
        return this.type;
    }
}
