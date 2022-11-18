package moodss.ia;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import moodss.ia.openal.AuxiliaryEffectManager;
import moodss.ia.openal.EAXReverbController;
import moodss.ia.openal.EchoController;
import moodss.ia.sfx.api.device.AudioDevice;
import moodss.ia.user.ImmersiveAudioConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public class ImmersiveAudio {
    /**
     * Main config
     */
    private final ImmersiveAudioConfig config;

    /**
     * Common EAX reverb controller
     */
    private final EAXReverbController eaxReverbController;

    /**
     * Common auxiliary effect handler
     */
    private final AuxiliaryEffectManager auxiliaryEffectManager;

    /**
     * Simple echo property handler
     */
    private final EchoController echoController;

    protected Logger LOGGER;

    public ImmersiveAudio(Path mainDirectory,
                          Consumer<Object2FloatMap<Object>> occlusionSanitizer,
                          Consumer<Object2FloatMap<Object>> exclusionSanitizer,
                          Consumer<Object2FloatMap<Object>> reflectivitySanitizer) {
        LOGGER = LoggerFactory.getLogger("ImmersiveAudio");

        var configPath = mainDirectory
                .resolve("immersive-audio-config.json");

        this.config = loadConfig(configPath, occlusionSanitizer, exclusionSanitizer, reflectivitySanitizer);

        //Initialise OpenAL components
        this.auxiliaryEffectManager = new AuxiliaryEffectManager();
        this.eaxReverbController = new EAXReverbController();
        this.echoController = new EchoController();
    }

    private static ImmersiveAudioConfig loadConfig(Path configPath,
                                                   Consumer<Object2FloatMap<Object>> occlusionSanitizer,
                                                   Consumer<Object2FloatMap<Object>> exclusionSanitizer,
                                                   Consumer<Object2FloatMap<Object>> reflectivitySanitizer) {
        try {
            System.out.println("Loading config");
            return ImmersiveAudioConfig.load(configPath, occlusionSanitizer, exclusionSanitizer, reflectivitySanitizer);
        } catch (Throwable t) {
            System.err.println("Failed to load configuration file for Immersive Audio " + t);
            var config = ImmersiveAudioConfig.defaults(configPath, occlusionSanitizer, exclusionSanitizer, reflectivitySanitizer);

            try {
                config.writeChanges();
            } catch (IOException ex) {
                throw new RuntimeException("Failed to replace configuration file with known-good defaults", ex);
            }

            return config;
        }
    }

    public void init(AudioDevice device, boolean sendAuto) {
        //Initialize auxiliary effects
        this.auxiliaryEffectManager.init(device, sendAuto);

        //Initialize EAXReverb effects
        this.eaxReverbController.init(device, this.auxiliaryEffectManager);

        //Initialize Echo effects
      //  this.echoController.init(device, this.auxiliaryEffectManager);
    }

    public ImmersiveAudioConfig config() {
        return this.config;
    }

    public AuxiliaryEffectManager auxiliaryEffectManager() {
        return this.auxiliaryEffectManager;
    }

    public EAXReverbController eaxReverbController() {
        return this.eaxReverbController;
    }

    public void destroy(AudioDevice device) {
        this.auxiliaryEffectManager.destroy(device);
        this.eaxReverbController.destroy(device);
       // this.echoController.destroy(device);
    }
}
