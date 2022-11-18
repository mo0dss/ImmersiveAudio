package moodss.ia.sfx.openal.device;

import moodss.ia.sfx.api.AudioException;
import moodss.ia.sfx.api.context.Context;
import moodss.ia.sfx.api.context.ContextDescription;
import moodss.ia.sfx.api.device.AudioDevice;
import moodss.ia.sfx.api.device.AudioDeviceContext;
import moodss.ia.sfx.api.device.AudioDeviceGate;
import moodss.ia.sfx.api.effects.AuxiliaryEffect;
import moodss.ia.sfx.api.effects.Effect;
import moodss.ia.sfx.api.effects.filter.Filter;
import moodss.ia.sfx.api.types.ALCToken;
import moodss.ia.sfx.api.effects.types.EffectType;
import moodss.ia.sfx.api.effects.types.FilterType;
import moodss.ia.sfx.openal.EfxEnum;
import moodss.ia.sfx.openal.context.AlcContext;
import moodss.ia.sfx.openal.effect.AlEfxAuxiliaryEffect;
import moodss.ia.sfx.openal.effect.AlEfxEffect;
import moodss.ia.sfx.openal.filter.AlEfxFilter;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.EXTEfx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlAudioDevice implements AudioDevice {

    private final Logger logger;

    /**
     * OpenAL device pointer
     */
    protected final long devicePtr;

    private final AudioDeviceContext context;

    /**
     * Simple flag for if the {@code "ALC_EXT_EFX"} extension is present
     */
    private final boolean efxSupported;

    public AlAudioDevice(long devicePtr) {
        this.context = new AlAudioDeviceContext(this);
        this.devicePtr = devicePtr;

        this.efxSupported = this.isExtensionPresent("ALC_EXT_EFX");

        this.logger = LoggerFactory.getLogger("AlAudioDevice");
    }

    public static long getDevicePointer(AudioDevice device) {
        return ((AlAudioDevice) device).devicePtr;
    }

    @Override
    public void run(AudioDeviceGate consumer) {
        try {
            consumer.apply(this.context);
        } catch(AudioException ex) {
            this.logger.error("{}: {}", ex.getMessage(), AL10.alGetString(AL10.alGetInteger(ex.getErrorCode())), ex);
        }
    }

    @Override
    public int getToken(ALCToken token) {
        return ALC10.alcGetInteger(this.devicePtr, EfxEnum.from(token));
    }

    @Override
    public void deleteFilter(Filter filter) {
        this.deleteFilter0((AlEfxFilter) filter);
    }

    protected void deleteFilter0(AlEfxFilter filter) {
        EXTEfx.alDeleteFilters(filter.getHandle());
        filter.invalidateHandle();
    }

    @Override
    public void deleteEffect(Effect effect) {
        this.deleteEffect0((AlEfxEffect) effect);
    }

    protected void deleteEffect0(AlEfxEffect effect) {
        EXTEfx.alDeleteEffects(effect.getHandle());
        effect.invalidateHandle();
    }

    @Override
    public void deleteAuxiliaryEffect(AuxiliaryEffect effect) {
        this.deleteAuxiliaryEffect0((AlEfxAuxiliaryEffect) effect);
    }

    protected void deleteAuxiliaryEffect0(AlEfxAuxiliaryEffect effect) {
        EXTEfx.alDeleteAuxiliaryEffectSlots(effect.getHandle());
        effect.invalidateHandle();
    }

    @Override
    public Context createContext(ContextDescription description) {
        var context = new AlcContext(this, description);

        int error = ALC10.alcGetError(this.devicePtr);
        if(error != ALC10.ALC_NO_ERROR) {
            RuntimeException ex = new RuntimeException("Failed creating efx filter");
            this.logger.error("{}: {}", ex.getMessage(), ALC10.alcGetString(this.devicePtr, ALC10.alcGetInteger(this.devicePtr, error)), ex);
            throw ex;
        }

        return context;
    }

    @Override
    public Filter createFilter(FilterType type) {
        if(!this.efxSupported) {
            return null;
        }

        var filter = new AlEfxFilter(type);

        int error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR) {
            RuntimeException ex = new RuntimeException("Failed creating efx filter");
            this.logger.error("{}: {}", ex.getMessage(), AL10.alGetString(AL10.alGetInteger(error)), ex);
            throw ex;
        }

        return filter;
    }

    @Override
    public Effect createEffect(EffectType type) {
        if(!this.efxSupported) {
            return null;
        }

        var effect = new AlEfxEffect(type);

        int error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR) {
            RuntimeException ex = new RuntimeException("Failed creating efx effect");
            this.logger.error("{}: {}", ex.getMessage(), AL10.alGetString(AL10.alGetInteger(error)), ex);
            throw ex;
        }

        return effect;
    }

    @Override
    public AuxiliaryEffect createAuxiliaryEffect(boolean sendAuto) {
        if(!this.efxSupported) {
            return null;
        }

        var handle = EXTEfx.alGenAuxiliaryEffectSlots();
        var effect = new AlEfxAuxiliaryEffect(handle, sendAuto);

        int error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR) {
            RuntimeException ex = new RuntimeException("Failed creating efx auxiliary effect");
            this.logger.error("{}: {}", ex.getMessage(), AL10.alGetString(AL10.alGetInteger(error)), ex);
            throw ex;
        }

        return effect;
    }

    @Override
    public boolean isExtensionPresent(String extension) {
        return ALC10.alcIsExtensionPresent(this.devicePtr, extension);
    }

    public boolean isEfxSupported() {
        return this.efxSupported;
    }
}
