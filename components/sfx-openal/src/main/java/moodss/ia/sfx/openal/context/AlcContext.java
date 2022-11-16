package moodss.ia.sfx.openal.context;

import moodss.ia.sfx.api.context.Context;
import moodss.ia.sfx.api.context.ContextDescription;
import moodss.ia.sfx.api.types.ALCToken;
import moodss.ia.sfx.openal.AlcEnum;
import moodss.ia.sfx.openal.AlcObject;
import moodss.ia.sfx.openal.device.AlAudioDevice;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class AlcContext extends AlcObject implements Context {

    public AlcContext(AlAudioDevice device, ContextDescription description) {
        var pointer = createContextAttributes(description.bindings());
        var handle = ALC10.nalcCreateContext(AlAudioDevice.getDevicePointer(device), pointer);
        this.setHandle(handle);

        MemoryUtil.nmemFree(pointer);
    }

    public AlcContext(long handle) {
        this.setHandle(handle);
    }

    public static long getHandle(Context context) {
        return ((AlcContext) context).getHandle();
    }

    protected static long createContextAttributes(ContextDescription.ContextBinding[] bindings) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            int bindingCount = bindings.length;
            int size = Integer.BYTES * bindingCount * 2;
            long pointer = stack.nmalloc(size);

            //Ensure all values are zero
            MemoryUtil.memSet(pointer, 0, size);

            ALCCapabilities capabilities = ALC.getCapabilities();
            for(int unit = 0; unit < bindingCount; unit+= 2) {
                ContextDescription.ContextBinding binding = bindings[unit];
                ALCToken token = binding.token();
                if(isSupported(token, capabilities)) {
                    MemoryUtil.memPutInt(pointer + (Integer.BYTES * unit), AlcEnum.from(token));
                    MemoryUtil.memPutInt(pointer + (Integer.BYTES * (unit + 1)), binding.value());
                }
            }

            return pointer;
        }
    }

    protected static boolean isSupported(ALCToken token, ALCCapabilities capabilities) {
        switch (token) {
            case HRTF -> {
                return capabilities.ALC_SOFT_HRTF;
            }
            case MAX_AUXILIARY_SENDS -> {
                return capabilities.ALC_EXT_EFX;
            }
        }

        return true;
    }
}
