package moodss.ia.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
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
    private void onRender(MatrixStack matrices, VertexConsumerProvider.Immediate buffers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if(!ImmersiveAudioMod.instance().config().raytracing.showDebug) {
            return;
        }

        Shader prevShader = RenderSystem.getShader();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        {
            RenderSystem.disableTexture();
            {
                RenderSystem.disableBlend();
                {
                    RenderSystem.lineWidth(1F);

                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder buffer = tessellator.getBuffer();

                    buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
                    {
                        PathtracedAudio audio = ImmersiveAudioMod.audioPathtracer();
                        audio.getDebug().forEachRay((ray) -> {
                            if(ray != null) {
                                buffer.vertex(ray.start().getX() - cameraX, ray.start().getY() - cameraY, ray.start().getZ() - cameraZ).color(ray.color()).next();
                                buffer.vertex(ray.to().getX() - cameraX, ray.to().getY() - cameraY, ray.to().getZ() - cameraZ).color(ray.color()).next();
                            }
                        });
                    }
                    tessellator.draw();
                }
                RenderSystem.enableBlend();
            }
            RenderSystem.enableTexture();
        }
        RenderSystem.setShader(() -> prevShader);
    }
}
