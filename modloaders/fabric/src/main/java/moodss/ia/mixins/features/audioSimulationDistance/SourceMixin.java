package moodss.ia.mixins.features.audioSimulationDistance;

import moodss.ia.ImmersiveAudioMod;
import net.minecraft.client.sound.Source;
import org.lwjgl.openal.AL10;
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

    @Inject(method = "setAttenuation", at = @At(value = "HEAD"))
    private void onSetAttenuation(float attenuation, CallbackInfo ci) {
        //I would like to formally apologize.
        AL10.alSourcef(this.pointer, AL10.AL_REFERENCE_DISTANCE, ImmersiveAudioMod.instance().config().world.minAudioSimulationDistance);
    }
}
