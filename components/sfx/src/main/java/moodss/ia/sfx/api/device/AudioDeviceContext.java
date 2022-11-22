package moodss.ia.sfx.api.device;

import moodss.ia.sfx.api.effects.AuxiliaryEffect;
import moodss.ia.sfx.api.effects.Effect;
import moodss.ia.sfx.api.effects.filter.Filter;
import moodss.ia.sfx.api.source.Source;
import moodss.ia.sfx.api.effects.types.EAXReverbProperties;
import moodss.ia.sfx.api.effects.types.EchoProperties;
import moodss.ia.sfx.api.effects.types.ReverbProperties;

public interface AudioDeviceContext {

    void setGain(Filter filter, float gain);

    void setGainLF(Filter filter, float gainLF);

    void setGainHF(Filter filter, float gainHF);

    void setReverb(Effect effect, ReverbProperties properties, float value);

    void setEAXReverb(Effect effect, EAXReverbProperties properties, float value);

    void setEAXReverb(Effect effect, EAXReverbProperties properties, float x, float y, float z);

    void setEcho(Effect effect, EchoProperties properties, float value);

    void setPosition(Source source, float x, float y, float z);

    void setDirection(Source source, float x, float y, float z);

    void bindAuxiliaryEffect(AuxiliaryEffect auxiliaryEffect, Effect effect);

    //TODO: Types
    void bindSourceAuxiliarySendFilter(Source source, AuxiliaryEffect auxiliaryEffect, Filter filter, int unit);

    void bindSourceSendFilter(Source source, Filter filter);
}
