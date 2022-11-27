package moodss.ia.mixins.features.stereoAngles;

import moodss.ia.client.ImmersiveAudioClientMod;
import moodss.ia.sfx.api.AudioException;
import moodss.ia.util.DirectionUtil;
import moodss.plummet.math.vec.Vector3;
import net.minecraft.client.sound.Source;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.EXTStereoAngles;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//TODO: Proper testing
@Mixin(Source.class)
public class SourceMixin {

    @Shadow
    @Final
    private int pointer;

    @Unique
    private boolean supported;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(int pointer, CallbackInfo ci) {
        this.supported = AL.getCapabilities().AL_EXT_STEREO_ANGLES;
    }

    //TODO: Audio Device
    @Inject(method = "setPosition", at = @At("RETURN"))
    private void onSetPosition(Vec3d pos, CallbackInfo ci) {
        if(this.supported) {
            return;
        }

        ImmersiveAudioClientMod.DEVICE.run(ctx -> {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                long xPtr = stack.ncalloc(Float.BYTES, 0, Float.BYTES);
                long yPtr = stack.ncalloc(Float.BYTES, 0, Float.BYTES);
                long zPtr = stack.ncalloc(Float.BYTES, 0, Float.BYTES);

                AL10.nalGetSource3f(this.pointer, AL10.AL_POSITION, xPtr, yPtr, zPtr);

                int error = AL10.alGetError();
                if(error != AL10.AL_NO_ERROR) {
                    throw new AudioException("Failed applying stereo angles", error);
                }

                float x = MemoryUtil.memGetFloat(xPtr);
                float y = MemoryUtil.memGetFloat(yPtr);
                float z = MemoryUtil.memGetFloat(zPtr);

                Direction direction = DirectionUtil.getFacing(Vector3.getFacing(x, y, z));
                long angleData = stack.nmalloc(Float.BYTES * 3);
                MemoryUtil.memPutFloat(angleData, direction.asRotation()); //Front
                MemoryUtil.memPutFloat(angleData + (Float.BYTES), direction.getOpposite().asRotation()); //Back

                AL10.nalSourcefv(this.pointer, EXTStereoAngles.AL_STEREO_ANGLES, angleData);

                //Update previous error with next error
                error = AL10.alGetError();
                if(error != AL10.AL_NO_ERROR) {
                    throw new AudioException("Failed applying stereo angles", error);
                }
            }
        });
    }
}
