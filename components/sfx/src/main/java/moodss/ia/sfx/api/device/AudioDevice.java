package moodss.ia.sfx.api.device;

import moodss.ia.sfx.api.types.ALCToken;

public interface AudioDevice extends ResourceFactory, ResourceDestructors {

    void run(AudioDeviceGate consumer);

    int getToken(ALCToken token);

    boolean isExtensionPresent(String extension);
}
