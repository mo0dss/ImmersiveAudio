package moodss.ia.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import moodss.ia.ImmersiveAudio;
import moodss.ia.ImmersiveAudioMod;
import moodss.ia.ray.PathtracedAudio;
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
    private void onRender(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {

        if(!ImmersiveAudio.CONFIG.raytracing.showDebug) {
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tesselator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuffer();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1F);

        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        PathtracedAudio audio = ImmersiveAudioMod.audioPathtracer();
        audio.getDebug().forEachRay((ray) -> {
            if(ray != null) {
                bufferBuilder.vertex(ray.start().getX() - cameraX, ray.start().getY() - cameraY, ray.start().getZ() - cameraZ).color(ray.color()).next();
                bufferBuilder.vertex(ray.to().getX() - cameraX, ray.to().getY() - cameraY, ray.to().getZ() - cameraZ).color(ray.color()).next();
            }
        });

        tesselator.draw();
        RenderSystem.lineWidth(1F);
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
    }
}
