package moodss.ia.sfx.openal.device;

import moodss.ia.sfx.api.AudioException;
import moodss.ia.sfx.api.device.AudioDeviceContext;
import moodss.ia.sfx.api.effect.AuxiliaryEffect;
import moodss.ia.sfx.api.effect.Effect;
import moodss.ia.sfx.api.filter.Filter;
import moodss.ia.sfx.api.source.Source;
import moodss.ia.sfx.api.types.*;
import moodss.ia.sfx.openal.ALException;
import moodss.ia.sfx.openal.EfxEnum;
import moodss.ia.sfx.openal.effect.AlEfxAuxiliaryEffect;
import moodss.ia.sfx.openal.effect.AlEfxEffect;
import moodss.ia.sfx.openal.filter.AlEfxFilter;
import moodss.ia.sfx.openal.source.AlSource;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.EXTEfx;

public class AlAudioDeviceContext implements AudioDeviceContext {

    private final AlAudioDevice device;

    public AlAudioDeviceContext(AlAudioDevice device) {
        this.device = device;
    }

    @Override
    public void setGain(Filter filter, float gain) {
        this.setGain0((AlEfxFilter) filter, gain);
    }

    protected void setGain0(AlEfxFilter filter, float gain) {
        var handle = filter.getHandle();

        switch(filter.type()) {
            case LOWPASS -> EXTEfx.alFilterf(handle, EfxEnum.from(LowpassFilterType.GAIN), gain);
            case HIGHPASS -> EXTEfx.alFilterf(handle, EfxEnum.from(HighpassFilterType.GAIN), gain);
            case BANDPASS -> EXTEfx.alFilterf(handle, EfxEnum.from(BandpassFilterType.GAIN), gain);
            case ALL -> {
                EXTEfx.alFilterf(handle, EfxEnum.from(LowpassFilterType.GAIN), gain);
                EXTEfx.alFilterf(handle, EfxEnum.from(HighpassFilterType.GAIN), gain);
                EXTEfx.alFilterf(handle, EfxEnum.from(BandpassFilterType.GAIN), gain);
            }
        }

        int error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR) {
            throw new AudioException("Failed applying efx filter gain", EfxEnum.from(error));
        }
    }

    @Override
    public void setGainLF(Filter filter, float gainLF) {
        this.setGainLF0((AlEfxFilter) filter, gainLF);
    }

    protected void setGainLF0(AlEfxFilter filter, float gainLF) {
        var handle = filter.getHandle();

        switch (filter.type()) {
            case HIGHPASS -> EXTEfx.alFilterf(handle, EfxEnum.from(HighpassFilterType.GAIN_LF), gainLF);
            case BANDPASS -> EXTEfx.alFilterf(handle, EfxEnum.from(BandpassFilterType.GAIN_LF), gainLF);
            case ALL -> {
                EXTEfx.alFilterf(handle, EfxEnum.from(HighpassFilterType.GAIN_LF), gainLF);
                EXTEfx.alFilterf(handle, EfxEnum.from(BandpassFilterType.GAIN_LF), gainLF);
            }
            case LOWPASS -> throw new AudioException("Lowpass filters do not support 'gainLF'", ErrorCondition.INVALID_OPERATION);
        }

        int error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR) {
            throw new AudioException("Failed applying efx filter gainLF", EfxEnum.from(error));
        }
    }

    @Override
    public void setGainHF(Filter filter, float gainHF) {
        this.setGainHF0((AlEfxFilter) filter, gainHF);
    }

    protected void setGainHF0(AlEfxFilter filter, float gainHF) {
        var handle = filter.getHandle();

        switch (filter.type()) {
            case HIGHPASS -> throw new AudioException("Highpass filters do not support 'gainHF'", ErrorCondition.INVALID_OPERATION);
            case LOWPASS -> EXTEfx.alFilterf(handle, EfxEnum.from(LowpassFilterType.GAIN_HF), gainHF);
            case BANDPASS -> EXTEfx.alFilterf(handle, EfxEnum.from(BandpassFilterType.GAIN_HF), gainHF);
            case ALL -> {
                EXTEfx.alFilterf(handle, EfxEnum.from(LowpassFilterType.GAIN_HF), gainHF);
                EXTEfx.alFilterf(handle, EfxEnum.from(BandpassFilterType.GAIN_LF), gainHF);
            }
        }

        int error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR) {
            throw new AudioException("Failed applying efx filter gainHF", EfxEnum.from(error));
        }
    }

    @Override
    public void setReverb(Effect effect, ReverbProperties properties, float value) {
        this.setReverb0((AlEfxEffect) effect, properties, value);
    }

    protected void setReverb0(AlEfxEffect effect, ReverbProperties properties, float value) {
        var handle = effect.getHandle();

        if(effect.type() != EffectType.REVERB) {
            throw new AudioException("%s effect filter type is not reverb".formatted(effect.type().name()), ErrorCondition.INVALID_OPERATION);
        }

        EXTEfx.alEffectf(handle, EfxEnum.from(properties), value);

        int error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR) {
            throw new AudioException("Failed applying reverb effect property %s".formatted(properties.name()), EfxEnum.from(error));
        }
    }

    @Override
    public void setEAXReverb(Effect effect, EAXReverbProperties properties, float value) {
        this.setEAXReverb0((AlEfxEffect) effect, properties, value);
    }

    protected void setEAXReverb0(AlEfxEffect effect, EAXReverbProperties properties, float value) {
        var handle = effect.getHandle();

        if(effect.type() != EffectType.EAXREVERB) {
            throw new AudioException("%s effect filter type is not eax reverb".formatted(effect.type().name()), ErrorCondition.INVALID_OPERATION);
        }

        EXTEfx.alEffectf(handle, EfxEnum.from(properties), value);

        int error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR) {
            throw new AudioException("Failed applying eax reverb effect property %s".formatted(properties.name()), EfxEnum.from(error));
        }
    }

    @Override
    public void setEcho(Effect effect, EchoProperties properties, float value) {
        this.setEcho0((AlEfxEffect) effect, properties, value);
    }

    protected void setEcho0(AlEfxEffect effect, EchoProperties properties, float value) {
        var handle = effect.getHandle();

        if(effect.type() != EffectType.ECHO) {
            throw new AudioException("%s effect filter type is not echo".formatted(effect.type().name()), ErrorCondition.INVALID_OPERATION);
        }

        EXTEfx.alEffectf(handle, EfxEnum.from(properties), value);

        int error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR) {
            throw new AudioException("Failed applying echo effect property %s".formatted(properties.name()), EfxEnum.from(error));
        }
    }

    @Override
    public void setPosition(Source source, float x, float y, float z) {
        this.setPosition0((AlSource) source, x, y, z);
    }

    protected void setPosition0(AlSource source, float x, float y, float z) {
        var handle = source.getHandle();

        AL10.alSource3f(handle, AL10.AL_POSITION, x, y, z);

        int error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR) {
            throw new AudioException("Failed applying source position", EfxEnum.from(error));
        }
    }

    @Override
    public void setDirection(Source source, float x, float y, float z) {
        this.setDirection0((AlSource) source, x, y, z);
    }

    protected void setDirection0(AlSource source, float x, float y, float z) {
        var handle = source.getHandle();

        AL10.alSource3f(handle, AL10.AL_DIRECTION, x, y, z);

        int error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR) {
            throw new AudioException("Failed applying source direction", EfxEnum.from(error));
        }
    }

    @Override
    public void bindAuxiliaryEffect(AuxiliaryEffect auxiliaryEffect, Effect effect) {
        if(!ALC10.alcIsExtensionPresent(this.device.devicePtr, "ALC_EXT_EFX")) {
            return;
        }

        this.bindAuxiliaryEffect0((AlEfxAuxiliaryEffect) auxiliaryEffect, (AlEfxEffect) effect);
    }

    protected void bindAuxiliaryEffect0(AlEfxAuxiliaryEffect auxiliaryEffect, AlEfxEffect effect) {
        var auxHandle = auxiliaryEffect.getHandle();
        var handle = effect.getHandle();

        EXTEfx.alAuxiliaryEffectSloti(auxHandle, EXTEfx.AL_EFFECTSLOT_EFFECT, handle);

        int error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR) {
            throw new AudioException("Failed binding auxiliary effect", EfxEnum.from(error));
        }
    }

    @Override
    public void bindSourceAuxiliarySendFilter(Source source, AuxiliaryEffect auxiliaryEffect, Filter filter, int unit) {
        this.bindSourceAuxiliarySendFilter0((AlSource) source, (AlEfxAuxiliaryEffect) auxiliaryEffect, (AlEfxFilter) filter, unit);
    }

    protected void bindSourceAuxiliarySendFilter0(AlSource source, AlEfxAuxiliaryEffect auxiliaryEffect, AlEfxFilter filter, int unit) {
        var srcHandle = source.getHandle();
        var auxHandle = auxiliaryEffect.getHandle();
        var handle = filter.getHandle();

        AL11.alSource3i(srcHandle, EXTEfx.AL_AUXILIARY_SEND_FILTER, auxHandle, unit, handle);

        int error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR) {
            throw new AudioException("Failed binding auxiliary send filter to source", EfxEnum.from(error));
        }
    }
}
