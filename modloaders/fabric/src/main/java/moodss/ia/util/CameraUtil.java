package moodss.ia.util;

import moodss.ia.client.camera.StoredCameraData;
import moodss.ia.fluid.FluidView;
import moodss.ia.fluid.ProjectedFluidView;
import moodss.plummet.math.vec.Vector3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.Validate;

public class CameraUtil {
    public static StoredCameraData<FluidView> getCameraData() {
        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = client.gameRenderer.getCamera();

        Validate.notNull(camera, "Camera is null?");
        Vec3d pos = camera.getPos();
        Vec3f orientation = camera.getHorizontalPlane();
        return new StoredCameraData<>(
                new Vector3((float) pos.x, (float) pos.y, (float) pos.z),
                new Vector3(orientation.getX(), orientation.getY(), orientation.getZ()),
                new StoredCameraData.Submersion<>(new ProjectedFluidView())
        );
    }
}
