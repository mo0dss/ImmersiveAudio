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
        long devicePointer = this.devicePointer;

        int specifiers = getInteger(devicePointer, SOFTHRTF.ALC_NUM_HRTF_SPECIFIERS_SOFT);
        if(specifiers == 0) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            int size = Integer.BYTES * 10;
            long pointer = stack.nmalloc(size);

            //Ensure all values are zero
            MemoryUtil.memSet(pointer, 0, size);

            MemoryUtil.memPutInt(pointer, SOFTHRTF.ALC_HRTF_SOFT);
            MemoryUtil.memPutInt(pointer + 4, enabled ? ALC10.ALC_TRUE : ALC10.ALC_FALSE);
            MemoryUtil.memPutInt(pointer + 8, SOFTHRTF.ALC_HRTF_ID_SOFT);

            if(!SOFTHRTF.nalcResetDeviceSOFT(devicePointer, pointer)) {
                LOGGER.warn("Failed to reset audio device: {}", ALC10.alcGetString(devicePointer, ALC10.alcGetError(devicePointer)));
            }

            MemoryUtil.nmemFree(pointer);
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
