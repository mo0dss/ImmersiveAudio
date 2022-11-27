package moodss.ia.mixins.features.soft;

import moodss.ia.client.ImmersiveAudioClientMod;
import moodss.ia.sfx.api.AudioException;
import net.minecraft.client.sound.Source;
import org.lwjgl.openal.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Source.class)
public class SourceMixin {

    @Shadow
    @Final
    private int pointer;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        int pointer = this.pointer;
        var capabilities = AL.getCapabilities();
        initialiseSpatialize(capabilities, pointer);
    }

    private static void initialiseSpatialize(ALCapabilities capabilities, int pointer) {
        if(!capabilities.AL_SOFT_source_spatialize) {
            return;
        }

        ImmersiveAudioClientMod.DEVICE.run(ctx -> {
            AL10.alSourcei(pointer, SOFTSourceSpatialize.AL_SOURCE_SPATIALIZE_SOFT, SOFTSourceSpatialize.AL_AUTO_SOFT);

            int error = AL10.alGetError();
            if(error != AL10.AL_NO_ERROR) {
                throw new AudioException("Failed applying soft spatialize", error);
            }
        });
    }

    private static void initialiseResampler(int pointer) {

    }
}
