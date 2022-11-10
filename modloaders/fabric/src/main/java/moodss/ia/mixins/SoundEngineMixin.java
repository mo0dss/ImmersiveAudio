package moodss.ia.mixins;

import moodss.ia.ImmersiveAudio;
import moodss.ia.sfx.openal.device.AlAudioDevice;
import net.minecraft.client.sound.SoundEngine;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.EXTEfx;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.IntBuffer;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Shadow
    private long devicePointer;

    @Redirect(method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/openal/ALC10;alcCreateContext(JLjava/nio/IntBuffer;)J"
            )
    )
    private long onInit$alcCreateContext(long deviceHandle, IntBuffer attrList) {
        //What.... is this..?
        return ALC10.alcCreateContext(deviceHandle, new int[]{ EXTEfx.ALC_MAX_AUXILIARY_SENDS, ImmersiveAudio.CONFIG.resolution, 0, 0});
    }

    @Inject(method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/openal/AL;createCapabilities(Lorg/lwjgl/openal/ALCCapabilities;)Lorg/lwjgl/openal/ALCapabilities;",
                    shift = At.Shift.AFTER,
                    remap = false
            )
    )
    private void onInit$makeContextCurrent(String deviceSpecifier, boolean directionalAudio, CallbackInfo ci) {
        //Ensure audioDevice is remade on every capability change.
        ImmersiveAudio.DEVICE = new AlAudioDevice(this.devicePointer);

        //Remake auxiliary effects
        ImmersiveAudio.createAuxiliaryEffects(true);

        //Remake EAX controller
        ImmersiveAudio.createEAXReverb(ImmersiveAudio.AUXILIARY_EFFECT_MANAGER);

        //Remake echo controller
        ImmersiveAudio.createEcho(ImmersiveAudio.AUXILIARY_EFFECT_MANAGER);
    }
}
