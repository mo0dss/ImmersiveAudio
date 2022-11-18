package moodss.ia.sfx.api.device;

import moodss.ia.sfx.api.context.Context;
import moodss.ia.sfx.api.context.ContextDescription;
import moodss.ia.sfx.api.effects.AuxiliaryEffect;
import moodss.ia.sfx.api.effects.Effect;
import moodss.ia.sfx.api.effects.filter.Filter;
import moodss.ia.sfx.api.effects.types.EffectType;
import moodss.ia.sfx.api.effects.types.FilterType;

public interface ResourceFactory {

    Context createContext(ContextDescription description);

    Filter createFilter(FilterType type);

    Effect createEffect(EffectType type);

    AuxiliaryEffect createAuxiliaryEffect(boolean sendAuto);
}
