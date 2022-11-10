package moodss.ia;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ImmersiveAudioMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ImmersiveAudio.init(FabricLoader.getInstance()
                .getConfigDir());
    }
}
