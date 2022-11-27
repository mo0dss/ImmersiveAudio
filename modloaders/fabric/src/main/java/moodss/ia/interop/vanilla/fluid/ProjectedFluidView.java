package moodss.ia.interop.vanilla.fluid;

import moodss.ia.fluid.FluidView;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.lang3.Validate;

public class ProjectedFluidView implements FluidView {

    private final boolean isInWater;

    public ProjectedFluidView() {
        MinecraftClient client = MinecraftClient.getInstance();
        Validate.notNull(client, "MinecraftClient has not been initialised");

        //Special case whenever a world hasn't been loaded.
        if(client.player == null) {
            this.isInWater = false;
            return;
        }

        this.isInWater = client.player.isSubmergedInWater();
    }

    @Override
    public boolean isWater() {
        return this.isInWater;
    }
}
