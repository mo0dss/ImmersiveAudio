package moodss.ia.mixins.core;

import moodss.ia.client.RayDebug;
import net.minecraft.client.render.*;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(MatrixStack matrices, VertexConsumerProvider.Immediate buffers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        RayDebug.renderDebug((float) cameraX, (float) cameraY, (float) cameraZ);
    }
}
