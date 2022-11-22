package moodss.ia.client.camera;

import moodss.ia.fluid.FluidView;
import moodss.plummet.math.vec.Vector3;

public record StoredCameraData<FLUID extends FluidView>(Vector3 position, Vector3 orientation, Submersion<FLUID> submersion) {

    public record Submersion<FLUID extends FluidView>(FLUID fluidType) {

    }
}
