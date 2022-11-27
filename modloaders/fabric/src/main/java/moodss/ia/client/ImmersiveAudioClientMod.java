package moodss.ia.client;

import moodss.ia.ImmersiveAudioMod;
import moodss.ia.interop.vanilla.ray.VanillaPathtracer;
import moodss.ia.sfx.api.device.AudioDevice;
import net.fabricmc.api.ClientModInitializer;

public class ImmersiveAudioClientMod implements ClientModInitializer {

    public static VanillaPathtracer PATHTRACER;

    public static AudioDevice DEVICE;

    @Override
    public void onInitializeClient() {
        var config = ImmersiveAudioMod.instance().config();

        if(config.eaxReverb) {
            PATHTRACER = new ReverbPathtracer(config, ImmersiveAudioMod.instance().eaxReverbController());
            return;
        }

        PATHTRACER = new VanillaPathtracer(config);
    }
}
