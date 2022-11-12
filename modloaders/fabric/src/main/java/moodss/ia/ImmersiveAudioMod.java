package moodss.ia;

import moodss.ia.ray.PathtracedAudio;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ImmersiveAudioMod implements ModInitializer {
    protected static PathtracedAudio AUDIO_PATHTRACER;

    @Override
    public void onInitialize() {
        ImmersiveAudio.init(FabricLoader.getInstance()
                .getConfigDir());

        AUDIO_PATHTRACER = new PathtracedAudio(ImmersiveAudio.CONFIG);
    }

    public static PathtracedAudio audioPathtracer() {
        return AUDIO_PATHTRACER;
    }
}
