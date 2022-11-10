package moodss.ia.sfx.api.device;

import moodss.ia.sfx.api.effect.AuxiliaryEffect;
import moodss.ia.sfx.api.effect.Effect;
import moodss.ia.sfx.api.filter.Filter;

public interface ResourceDestructors {

    void deleteFilter(Filter filter);

    void deleteEffect(Effect effect);

    void deleteAuxiliaryEffect(AuxiliaryEffect effect);
}
