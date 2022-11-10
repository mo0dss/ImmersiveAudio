package moodss.ia.ray.v2;

import moodss.ia.ray.Ray;
import moodss.ia.user.ImmersiveAudioConfig;
import moodss.plummet.math.vec.Vector3;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

public class CollisionPathtracer {

    private final BiDirectionalPathtracer pathtracer;

    public CollisionPathtracer(ImmersiveAudioConfig.Raytracing raytracing) {

        this.pathtracer = new BiDirectionalPathtracer(raytracing);
    }

    public <T> CompletableFuture<Vector3> computePosition(Vector3 origin, Vector3 listener, float maxDistance, Executor executor) {
        return null;
    }
}
