package moodss.ia.mixins.features.radius;

import moodss.ia.ImmersiveAudioMod;
import moodss.ia.client.ImmersiveAudioClientMod;
import moodss.ia.sfx.api.AudioException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.Source;
import org.lwjgl.openal.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Source.class)
public class SourceMixin {

    @Shadow
    @Final
    private int pointer;

    @Unique
    private boolean supported;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(int pointer, CallbackInfo ci) {
        this.supported = AL.getCapabilities().AL_EXT_SOURCE_RADIUS;
    }

    @Inject(method = "setAttenuation", at = @At("RETURN"))
    private void onSetAttenuation(float attenuation, CallbackInfo ci) {
        if(!this.supported) {
            return;
        }

        int pointer = this.pointer;

        ImmersiveAudioClientMod.DEVICE.run(ctx -> {
            var config = ImmersiveAudioMod.instance().config();
            AL10.alSourcef(pointer, EXTSourceRadius.AL_SOURCE_RADIUS, config.world.maxAudioSimulationDistance(MinecraftClient.getInstance().options.getSimulationDistance().getValue()));

            int error = AL10.alGetError();
            if(error != AL10.AL_NO_ERROR) {
                throw new AudioException("Failed applying source radius", error);
            }
        });
    }
}
