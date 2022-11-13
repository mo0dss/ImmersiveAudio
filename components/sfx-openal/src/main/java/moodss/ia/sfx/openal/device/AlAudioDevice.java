package moodss.ia.sfx.openal.device;

import moodss.ia.sfx.api.AudioException;
import moodss.ia.sfx.api.device.AudioDevice;
import moodss.ia.sfx.api.device.AudioDeviceContext;
import moodss.ia.sfx.api.device.AudioDeviceGate;
import moodss.ia.sfx.api.effect.AuxiliaryEffect;
import moodss.ia.sfx.api.effect.Effect;
import moodss.ia.sfx.api.filter.Filter;
import moodss.ia.sfx.api.types.ALCToken;
import moodss.ia.sfx.api.types.EffectType;
import moodss.ia.sfx.api.types.ErrorCondition;
import moodss.ia.sfx.api.types.FilterType;
import moodss.ia.sfx.openal.AlTags;
import moodss.ia.sfx.openal.EfxEnum;
import moodss.ia.sfx.openal.effect.AlEfxAuxiliaryEffect;
import moodss.ia.sfx.openal.effect.AlEfxEffect;
import moodss.ia.sfx.openal.filter.AlEfxFilter;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.EXTEfx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.LogManager;

public class AlAudioDevice implements AudioDevice {

    private final Logger logger;

    protected final long devicePtr;
    private final AudioDeviceContext context;

    public AlAudioDevice(long devicePtr) {
        this.context = new AlAudioDeviceContext(this);
        this.devicePtr = devicePtr;

        this.logger = LoggerFactory.getLogger("AlAudioDevice");
    }

    @Override
    public void run(AudioDeviceGate consumer) {
        try {
            consumer.apply(this.context);
        } catch(AudioException ex) {
            ErrorCondition condition = ex.getCondition();
            this.logger.error("{}: OpenAL error {}", ex.getMessage(), AlTags.from(condition), ex);

        //   throw new RuntimeException("%s: OpenAL error %s".formatted(ex.getMessage(), AlTags.from(condition)));
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
    public Filter createFilter(FilterType type) {
        if(!ALC10.alcIsExtensionPresent(this.devicePtr, "ALC_EXT_EFX")) {
            return null;
        }

        return new AlEfxFilter(type);
    }

    @Override
    public Effect createEffect(EffectType type) {
        if(!ALC10.alcIsExtensionPresent(this.devicePtr, "ALC_EXT_EFX")) {
            return null;
        }

        return new AlEfxEffect(type);
    }

    @Override
    public AuxiliaryEffect createAuxiliaryEffect(boolean sendAuto) {
        if(!ALC10.alcIsExtensionPresent(this.devicePtr, "ALC_EXT_EFX")) {
            return null;
        }

        return new AlEfxAuxiliaryEffect(sendAuto);
    }
}
