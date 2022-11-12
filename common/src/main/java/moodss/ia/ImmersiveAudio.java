package moodss.ia;

import moodss.ia.openal.AuxiliaryEffectManager;
import moodss.ia.openal.EAXReverbController;
import moodss.ia.openal.EchoController;
import moodss.ia.sfx.api.device.AudioDevice;
import moodss.ia.user.ImmersiveAudioConfig;

import java.io.IOException;
import java.nio.file.Path;

public class ImmersiveAudio {
    public static AudioDevice DEVICE;
    public static ImmersiveAudioConfig CONFIG;
    public static EAXReverbController EAX_REVERB_CONTROLLER;
    public static AuxiliaryEffectManager AUXILIARY_EFFECT_MANAGER;
    public static EchoController ECHO_CONTROLLER;

    public static void init(Path mainDirectory) {
        var configPath = mainDirectory
                .resolve("immersive-audio-config.json");

        loadConfig(configPath);
    }

    public static void createAuxiliaryEffects(boolean sendAuto) {
        if(AUXILIARY_EFFECT_MANAGER != null) {
            AUXILIARY_EFFECT_MANAGER.destroy();
        }

        AUXILIARY_EFFECT_MANAGER = new AuxiliaryEffectManager(ImmersiveAudio.DEVICE, sendAuto);
    }

    public static void createEcho(AuxiliaryEffectManager auxiliaryEffectManager) {
        if(ECHO_CONTROLLER != null) {
            ECHO_CONTROLLER.destroy();
        }

        ECHO_CONTROLLER = new EchoController(ImmersiveAudio.DEVICE, auxiliaryEffectManager);
    }

    public static void createEAXReverb(AuxiliaryEffectManager auxiliaryEffectManager) {
        if(EAX_REVERB_CONTROLLER != null) {
            EAX_REVERB_CONTROLLER.destroy();
        }

        EAX_REVERB_CONTROLLER = new EAXReverbController(ImmersiveAudio.DEVICE, auxiliaryEffectManager);
    }

    private static void loadConfig(Path configPath) {
        try {
            System.out.println("Loading config");
            CONFIG = ImmersiveAudioConfig.load(configPath);
        } catch (Throwable t) {
            System.err.println("Failed to load configuration file for Immersive Audio " + t);
            CONFIG = ImmersiveAudioConfig.defaults(configPath);

            try {
                CONFIG.writeChanges();
            } catch (IOException ex) {
                throw new RuntimeException("Failed to replace configuration file with known-good defaults", ex);
            }
        }
    }
}
