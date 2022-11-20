package moodss.ia;

import moodss.ia.ray.PathtracedAudio;
import moodss.ia.util.BlockSoundGroupAttributes;
import moodss.ia.util.SupportedSoundTypeUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ImmersiveAudioMod implements ModInitializer {

    protected static ImmersiveAudio INSTANCE;

    protected static PathtracedAudio AUDIO_PATHTRACER;

    @Override
    public void onInitialize() {
        BlockSoundGroupAttributes.init();
        SupportedSoundTypeUtil.init();

        INSTANCE = new ImmersiveAudio(
                FabricLoader.getInstance().getConfigDir(),
                BlockSoundGroupAttributes::applyOcclusion,
                BlockSoundGroupAttributes::applyExclusion,
                BlockSoundGroupAttributes::applyReflectivity
        );

        AUDIO_PATHTRACER = new PathtracedAudio(INSTANCE.config());
    }

    public static ImmersiveAudio instance() {
        if(INSTANCE == null) {
            throw new RuntimeException("ImmersiveAudio instance not yet initialized.");
        }
        return INSTANCE;
    }

    public static PathtracedAudio audioPathtracer() {
        return AUDIO_PATHTRACER;
    }
}
