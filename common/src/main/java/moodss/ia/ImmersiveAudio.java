package moodss.ia;

import moodss.ia.openal.AuxiliaryEffectManager;
import moodss.ia.openal.EAXReverbController;
import moodss.ia.openal.EchoController;
import moodss.ia.sfx.api.device.AudioDevice;
import moodss.ia.user.ImmersiveAudioConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

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

    protected static Logger LOGGER;

    public ImmersiveAudio(Path mainDirectory, ImmersiveAudioConfig.Sanitizer sanitizer) {
        LOGGER = LoggerFactory.getLogger("ImmersiveAudio");

        var configPath = mainDirectory
                .resolve("immersive-audio-config.json");

        this.config = loadConfig(configPath, sanitizer);

        //Initialise OpenAL components
        this.auxiliaryEffectManager = new AuxiliaryEffectManager();
        this.eaxReverbController = new EAXReverbController();
        this.echoController = new EchoController();
    }

    private static ImmersiveAudioConfig loadConfig(Path configPath, ImmersiveAudioConfig.Sanitizer sanitizer) {
        try {
            LOGGER.info("Loading config");
            return ImmersiveAudioConfig.load(configPath, sanitizer);
        } catch (Throwable t) {
            LOGGER.warn("Failed to load configuration file for Immersive Audio", t);
            var config = ImmersiveAudioConfig.defaults(configPath, sanitizer);

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
