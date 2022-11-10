package moodss.ia.sfx.api.device;

import moodss.ia.sfx.api.effect.AuxiliaryEffect;
import moodss.ia.sfx.api.effect.Effect;
import moodss.ia.sfx.api.filter.Filter;
import moodss.ia.sfx.api.types.EffectType;
import moodss.ia.sfx.api.types.FilterType;

public interface ResourceFactory {

    Filter createFilter(FilterType type);

    Effect createEffect(EffectType type);

    AuxiliaryEffect createAuxiliaryEffect(boolean sendAuto);
}
