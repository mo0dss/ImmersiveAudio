package moodss.ia.mixins.speedup;

import com.mojang.logging.LogUtils;
import net.minecraft.client.sound.SoundEngine;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.SOFTHRTF;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(method = "getMonoSourceCount", at = @At("HEAD"), cancellable = true)
    private void onGetMonoSourceCount(CallbackInfoReturnable<Integer> cir) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            int attributes = getInteger(this.devicePointer, ALC10.ALC_ATTRIBUTES_SIZE);

            if (checkALCErrors(this.devicePointer, "Get attributes size")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }

            long statusPointer = stack.ncalloc(Integer.BYTES, attributes, Integer.BYTES);
            ALC10.nalcGetIntegerv(this.devicePointer, ALC10.ALC_ALL_ATTRIBUTES, attributes, statusPointer);

            if (checkALCErrors(this.devicePointer, "Get attributes")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }

            int currentAttribute = 0;

            while(currentAttribute < attributes) {
                int lowerAttribute = MemoryUtil.memGetInt(statusPointer + (Integer.BYTES * currentAttribute++));
                if(lowerAttribute == 0) {
                    break;
                }

                int upperAttribute = MemoryUtil.memGetInt(statusPointer + (Integer.BYTES * currentAttribute++));
                if(upperAttribute == ALC11.ALC_MONO_SOURCES) {
                    cir.setReturnValue(upperAttribute);
                }
            }
        }
    }

    private static boolean checkALCErrors(long deviceHandle, String sectionName) {
        int error = ALC10.alcGetError(deviceHandle);
        if(error != 0) {
            LogUtils.getLogger().error("{}{}: {}", sectionName, deviceHandle, AL10.alGetString(AL10.alGetInteger(error)));
            return true;
        }
        return false;
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
