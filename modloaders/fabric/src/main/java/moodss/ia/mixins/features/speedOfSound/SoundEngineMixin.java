package moodss.ia.mixins.features.speedOfSound;

import moodss.ia.ImmersiveAudioMod;
import moodss.ia.client.ImmersiveAudioClientMod;
import moodss.ia.sfx.api.AudioException;
import net.minecraft.client.sound.SoundEngine;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL11;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(String deviceSpecifier, boolean directionalAudio, CallbackInfo ci) {
        if(AL.getCapabilities().alSpeedOfSound == MemoryUtil.NULL) {
            return;
        }

        ImmersiveAudioClientMod.DEVICE.run(ctx -> {
            AL11.alSpeedOfSound(ImmersiveAudioMod.instance().config().world.speedOfSound);
            int error = AL11.alGetError();
            if(error != AL11.AL_NO_ERROR) {
                throw new AudioException("Unable to modify speed of sound", error);
            }
        });
    }
}
