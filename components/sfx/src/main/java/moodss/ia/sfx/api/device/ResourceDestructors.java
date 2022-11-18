package moodss.ia.sfx.api.device;

import moodss.ia.sfx.api.effects.AuxiliaryEffect;
import moodss.ia.sfx.api.effects.Effect;
import moodss.ia.sfx.api.effects.filter.Filter;

public interface ResourceDestructors {

    void deleteFilter(Filter filter);

    void deleteEffect(Effect effect);

    void deleteAuxiliaryEffect(AuxiliaryEffect effect);
}
