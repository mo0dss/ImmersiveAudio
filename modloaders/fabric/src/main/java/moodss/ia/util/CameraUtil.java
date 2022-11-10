package moodss.ia.util;

import moodss.plummet.math.vec.Vector3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.Validate;

public class CameraUtil {

    public static Vector3 getActiveCameraPosition() {
        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = client.gameRenderer.getCamera();

        Validate.notNull(camera, "Camera is null?");
        Vec3d pos = camera.getPos();
        return new Vector3((float) pos.x, (float) pos.y, (float) pos.z);
    }
}
