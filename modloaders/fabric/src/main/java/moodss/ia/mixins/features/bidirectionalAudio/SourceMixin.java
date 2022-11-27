package moodss.ia.mixins.features.bidirectionalAudio;

import moodss.ia.ImmersiveAudioMod;
import moodss.ia.client.ImmersiveAudioClientMod;
import moodss.ia.sfx.openal.source.AlSource;
import moodss.ia.user.ImmersiveAudioConfig;
import moodss.ia.interop.vanilla.ray.BlockTraceCollisionUtil;
import moodss.ia.util.CameraUtil;
import moodss.plummet.math.vec.Vector3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.Source;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
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
        ImmersiveAudioConfig config = ImmersiveAudioMod.instance().config();
        if(!config.bidirectionalAudio) {
            return;
        }

        Vector3 position = new Vector3((float) pos.x, (float) pos.y, (float) pos.z);

        AlSource source = AlSource.wrap(this.pointer);
        var cameraData = CameraUtil.getCameraData();

        Vector3 computedPosition = ImmersiveAudioClientMod.PATHTRACER.computePathtrace(
                        position,
                        cameraData.position(),
                        BlockTraceCollisionUtil::createCollision,
                        config.raytracing.maxRayDistance(config.world.maxAudioSimulationDistance(MinecraftClient.getInstance().options.getSimulationDistance().getValue())),
                        Util.getMainWorkerExecutor()
                )
                .join();

        ImmersiveAudioClientMod.DEVICE.run(ctx -> {
            if (!computedPosition.equals(Vector3.ZERO)) {
                ctx.setPosition(source, computedPosition.getX(), computedPosition.getY(), computedPosition.getZ());
                ci.cancel();
            }
        });
    }
}
