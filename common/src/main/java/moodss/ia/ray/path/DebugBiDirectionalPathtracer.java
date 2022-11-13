package moodss.ia.ray.path;

import moodss.ia.ray.Ray;
import moodss.ia.ray.RayHitResult;
import moodss.ia.ray.trace.Raytracer;
import moodss.ia.user.ImmersiveAudioConfig;
import moodss.plummet.math.vec.Vector3;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DebugBiDirectionalPathtracer extends BiDirectionalPathtracer {

    /**
     * Pathtracer debug
     */
    private final PathtracedDebug debug;

    public DebugBiDirectionalPathtracer(ImmersiveAudioConfig.Raytracing raytracing) {
        super(raytracing.maxRayCount, raytracing.additionalRayCount, raytracing.maxRayBounceCount, raytracing.additionalRayBounceCount);

        this.debug = new PathtracedDebug(raytracing.maxRayCount, raytracing.maxRayBounceCount, raytracing.additionalRayCount, raytracing.additionalRayBounceCount, raytracing.showDebug);
    }

    public PathtracedDebug getDebug() {
        return this.debug;
    }

    public CompletableFuture<Vector3> pathtrace(Vector3 origin,
                                                Vector3 listener,
                                                Raytracer traceFunc,
                                                float maxDistance,
                                                Executor executor) {
        this.debug.clear();

        return super.computePathtrace(origin, listener, traceFunc, maxDistance, executor);
    }

    @Override
    protected void onRayBounceStart(RayHitResult result, Ray ray) {
        this.debug.addRay(Ray.getOrigin(ray), Ray.getOrigin(result.ray()), 0xFF55FF55);
    }

    @Override
    protected void onRayBounceMiss(Ray ray, Vector3 endPosition) {
        this.debug.addRay(Ray.getOrigin(ray), endPosition, 0xFFFF5555);
    }

    @Override
    protected void onRayBounceHit(RayHitResult result, Ray ray, Vector3 endPosition, int unit) {
        this.debug.addRay(Ray.getOrigin(result.ray()), endPosition, 0xFF0000FF);
    }
}
