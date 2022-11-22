package moodss.ia.mixins;

import moodss.ia.ImmersiveAudioMod;
import moodss.ia.client.ImmersiveAudioClientMod;
import moodss.ia.sfx.api.AudioException;
import moodss.ia.sfx.api.context.ContextDescription;
import moodss.ia.sfx.api.types.ALCToken;
import moodss.ia.sfx.openal.context.AlcContext;
import moodss.ia.sfx.openal.device.AlAudioDevice;
import moodss.ia.user.ImmersiveAudioConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundEngine;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC10;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.IntBuffer;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Redirect(method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/openal/ALC10;alcCreateContext(JLjava/nio/IntBuffer;)J"
            )
    )
    private long onInit$alcCreateContext(long deviceHandle, IntBuffer attrList) {
        //Ensure device is remade on every context change.
        var device = ImmersiveAudioClientMod.DEVICE = new AlAudioDevice(deviceHandle);

        ImmersiveAudioConfig config = ImmersiveAudioMod.instance().config();

        return AlcContext.getHandle(device.createContext(new ContextDescription(new ContextDescription.ContextBinding[] {
                new ContextDescription.ContextBinding(ALCToken.MAX_AUXILIARY_SENDS, config.audioResolution),
                new ContextDescription.ContextBinding(ALCToken.HRTF, MinecraftClient.getInstance().options.getDirectionalAudio().getValue() ? ALC10.ALC_TRUE : ALC10.ALC_FALSE),
                new ContextDescription.ContextBinding(ALCToken.MONO, config.monoSources),
                new ContextDescription.ContextBinding(ALCToken.STEREO, config.stereoSources)
        })));
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(String deviceSpecifier, boolean directionalAudio, CallbackInfo ci) {
        ImmersiveAudioMod.instance().init(ImmersiveAudioClientMod.DEVICE, true);

        AL11.alSpeedOfSound(ImmersiveAudioMod.instance().config().world.speedOfSound);
        int error = AL11.alGetError();
        if(error != AL11.AL_NO_ERROR) {
            throw new AudioException("Unable to modify speed of sound", error);
        }
    }

    @Inject(method = "close", at = @At("RETURN"))
    private void onClose(CallbackInfo ci) {
        ImmersiveAudioMod.instance().destroy(ImmersiveAudioClientMod.DEVICE);
    }
}
