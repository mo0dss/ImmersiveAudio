package moodss.ia.mixins;

import moodss.ia.ImmersiveAudio;
import moodss.ia.sfx.openal.source.AlSource;
import moodss.ia.util.BlockTraceCollisionUtil;
import moodss.ia.util.CameraUtil;
import moodss.ia.util.ReflectivityUtil;
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
        Vector3 position = new Vector3((float) pos.x, (float) pos.y, (float) pos.z);
        Vector3 cameraPosition = CameraUtil.getActiveCameraPosition();
        Vector3 computedOrigin = ImmersiveAudio.EAX_REVERB_CONTROLLER.computeOriginFor(
                AlSource.wrap(this.pointer),
                position,
                cameraPosition,
                BlockTraceCollisionUtil::createCollision,
                ReflectivityUtil::getReflectivity,
                ImmersiveAudio.CONFIG.world.maxAudioSimulationDistance(MinecraftClient.getInstance().options.getSimulationDistance().getValue()),
                Util.getMainWorkerExecutor()
        );

        AlSource source = AlSource.wrap(this.pointer);

        ImmersiveAudio.DEVICE.run(context -> {
            if(!computedOrigin.equals(Vector3.ZERO)) {
                System.out.println("Raytraced sound position : " + computedOrigin.toString());
                context.setPosition(source, computedOrigin.getX(), computedOrigin.getY(), computedOrigin.getZ());

                Vector3 direction = Vector3.getFacing(computedOrigin);
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

    @Inject(method = "play", at = @At("HEAD"))
    private void onPlay(CallbackInfo ci) {
    }
}
