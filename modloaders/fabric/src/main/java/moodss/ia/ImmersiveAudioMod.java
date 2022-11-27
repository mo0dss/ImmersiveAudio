package moodss.ia;

import moodss.ia.util.BlockSoundGroupSanitizer;
import moodss.ia.util.SupportedSoundTypeUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ImmersiveAudioMod implements ModInitializer {

    protected static ImmersiveAudio INSTANCE;

    @Override
    public void onInitialize() {
        BlockSoundGroupSanitizer.init();
        SupportedSoundTypeUtil.init();

        INSTANCE = new ImmersiveAudio(
                FabricLoader.getInstance().getConfigDir(),
                new BlockSoundGroupSanitizer()
        );
    }

    public static ImmersiveAudio instance() {
        if(INSTANCE == null) {
            throw new RuntimeException("ImmersiveAudio instance not yet initialized.");
        }
        return INSTANCE;
    }
}
