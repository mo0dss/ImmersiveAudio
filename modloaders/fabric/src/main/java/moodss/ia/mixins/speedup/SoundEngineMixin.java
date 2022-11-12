package moodss.ia.mixins.speedup;

import net.minecraft.client.sound.SoundEngine;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.SOFTHRTF;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @Shadow
    private long devicePointer;

    @Shadow
    @Final
    static Logger LOGGER;

    /**
     * @author Mo0dss
     * @reason Don't create buffers
     */
    @Overwrite
    private void setDirectionalAudio(boolean enabled) {
        int specifiers = getInteger(this.devicePointer, SOFTHRTF.ALC_NUM_HRTF_SPECIFIERS_SOFT);
        if(specifiers == 0) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            long pointer = stack.nmalloc(10);

            MemoryUtil.memPutInt(pointer, SOFTHRTF.ALC_HRTF_SOFT);
            MemoryUtil.memPutInt(pointer + 1, enabled ? 1 : 0);
            MemoryUtil.memPutInt(pointer + 2, SOFTHRTF.ALC_HRTF_ID_SOFT);

            //TODO: Is this neccessary?
            MemoryUtil.memPutInt(pointer + 3, 0);
            MemoryUtil.memPutInt(pointer + 4, 0);

            if(!SOFTHRTF.nalcResetDeviceSOFT(this.devicePointer, pointer)) {
                LOGGER.warn("Failed to reset audio device: {}", ALC10.alcGetString(this.devicePointer, ALC10.alcGetError(this.devicePointer)));
            }
        }
    }

    @Unique
    private static int getInteger(long devicePointer, int token) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long statusPointer = stack.ncalloc(Integer.BYTES, 0, Integer.BYTES);
            ALC10.nalcGetIntegerv(devicePointer, token, 1, statusPointer);

            return MemoryUtil.memGetInt(statusPointer);
        }
    }
}
