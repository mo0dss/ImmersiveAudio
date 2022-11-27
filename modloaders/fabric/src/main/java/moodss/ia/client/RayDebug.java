package moodss.ia.client;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import moodss.ia.ImmersiveAudioMod;
import moodss.plummet.math.vec.Vector3;
import net.minecraft.client.render.*;
import net.minecraft.util.Util;

import java.util.List;

public class RayDebug {

    private static final List<Ray> rays = new ObjectArrayList<>();

    public static void recordRay(Vector3 from, Vector3 to, int color) {
        var time = Util.getMeasuringTimeMs();

        rays.add(new Ray(from, to, color, time));

        rays.removeIf(ray -> (time - ray.startTime) > ImmersiveAudioMod.instance().config().raytracing.maxRayCount);
    }

    public static void renderDebug(float cameraX, float cameraY, float cameraZ) {
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
                        synchronized (rays) {
                            for(Ray ray : rays) {
                                buffer.vertex(ray.start().getX() - cameraX, ray.start().getY() - cameraY, ray.start().getZ() - cameraZ).color(ray.color()).next();
                                buffer.vertex(ray.to().getX() - cameraX, ray.to().getY() - cameraY, ray.to().getZ() - cameraZ).color(ray.color()).next();
                            }
                        }
                    }
                    tessellator.draw();
                }
                RenderSystem.enableBlend();
            }
            RenderSystem.enableTexture();
        }
        RenderSystem.setShader(() -> prevShader);
    }

    public record Ray(Vector3 start, Vector3 to, int color, long startTime)
    {}
}
