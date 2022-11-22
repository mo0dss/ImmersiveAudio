package moodss.ia.mixins;

import moodss.ia.ImmersiveAudio;
import moodss.ia.ImmersiveAudioMod;
import moodss.ia.client.ImmersiveAudioClientMod;
import moodss.ia.ray.PathtracedAudio;
import moodss.ia.ray.Ray;
import moodss.ia.ray.RayHitResult;
import moodss.ia.ray.trace.Raytracer;
import moodss.ia.sfx.openal.source.AlSource;
import moodss.ia.user.ImmersiveAudioConfig;
import moodss.ia.util.BlockTraceCollisionUtil;
import moodss.ia.util.CameraUtil;
import moodss.plummet.math.vec.Vector3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.Source;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
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

        PathtracedAudio pathtracer = ImmersiveAudioMod.audioPathtracer();
        AlSource source = AlSource.wrap(this.pointer);

        ImmersiveAudioConfig config = ImmersiveAudioMod.instance().config();

        Vector3 computedPosition = pathtracer.computePathtrace(
                        position,
                        CameraUtil.getCameraData(),
                        BlockTraceCollisionUtil::createCollision,
                        config.raytracing.maxRayDistance(config.world.maxAudioSimulationDistance(MinecraftClient.getInstance().options.getSimulationDistance().getValue())),
                        Util.getMainWorkerExecutor()
                )
                .join();

        ImmersiveAudioClientMod.DEVICE.run(context -> {
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
        AL10.alSourcef(this.pointer, AL10.AL_REFERENCE_DISTANCE, ImmersiveAudioMod.instance().config().world.minAudioSimulationDistance);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(int pointer, CallbackInfo ci) {
        AlSource source = AlSource.wrap(pointer);
        ImmersiveAudioClientMod.DEVICE.run(context -> {
            ImmersiveAudio commons = ImmersiveAudioMod.instance();
            commons.eaxReverbController().applyToSource(context, commons.auxiliaryEffectManager(), source);
        });
    }
}
