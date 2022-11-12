package moodss.ia.mixins;

import moodss.ia.ImmersiveAudio;
import moodss.ia.ImmersiveAudioMod;
import moodss.ia.ray.PathtracedAudio;
import moodss.ia.sfx.openal.source.AlSource;
import moodss.ia.util.BlockTraceCollisionUtil;
import moodss.ia.util.CameraUtil;
import moodss.plummet.math.vec.Vector3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.Source;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
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

    @Inject(method = "setPosition", at = @At("HEAD"), cancellable = true)
    private void onSetPosition(Vec3d pos, CallbackInfo ci) {
        Vector3 position = new Vector3((float)pos.x, (float)pos.y, (float)pos.z);
        Vector3 cameraPosition = CameraUtil.getActiveCameraPosition();

        PathtracedAudio pathtracer = ImmersiveAudioMod.audioPathtracer();
        AlSource source = AlSource.wrap(this.pointer);

        Vector3 computedPosition = pathtracer.pathtrace(
                        position,
                        cameraPosition,
                        BlockTraceCollisionUtil::createCollision,
                        ImmersiveAudio.CONFIG.world.maxAudioSimulationDistance(MinecraftClient.getInstance().options.getSimulationDistance().getValue()),
                        Util.getMainWorkerExecutor()
                )
                .join();

        ImmersiveAudio.DEVICE.run(context -> {
            if (!computedPosition.equals(Vector3.ZERO)) {
                context.setPosition(source, computedPosition.getX(), computedPosition.getY(), computedPosition.getZ());

                Vector3 direction = Vector3.getFacing(computedPosition);
                context.setDirection(source, direction.getX(), direction.getY(), direction.getZ());

                ci.cancel();
            }
        });
    }


    @Inject(method = "setAttenuation", at = @At(value = "HEAD"))
    private void onSetAttenuation(float attenuation, CallbackInfo ci) {
        //I would like to formally apologize.
        AL10.alSourcef(this.pointer, AL10.AL_REFERENCE_DISTANCE, ImmersiveAudio.CONFIG.world.minAudioSimulationDistance);
    }

    @Inject(method = "play", at = @At("RETURN"))
    private void onPlay(CallbackInfo ci) {
        AlSource source = AlSource.wrap(this.pointer);
        ImmersiveAudio.DEVICE.run(context -> ImmersiveAudio.EAX_REVERB_CONTROLLER.applyToSource(context, ImmersiveAudio.AUXILIARY_EFFECT_MANAGER, source));
    }
}
