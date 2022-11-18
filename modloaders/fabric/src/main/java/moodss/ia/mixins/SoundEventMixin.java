package moodss.ia.mixins;

import moodss.ia.ImmersiveAudioMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEvent.class)
public class SoundEventMixin {

    @Inject(method = "getDistanceToTravel", at = @At("RETURN"), cancellable = true)
    private void onGetDistanceToTravel(float volume, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(Math.min(cir.getReturnValue(), ImmersiveAudioMod.instance().config().world.maxAudioSimulationDistance(
                MinecraftClient.getInstance().options.getSimulationDistance().getValue()
        )));
    }
}
