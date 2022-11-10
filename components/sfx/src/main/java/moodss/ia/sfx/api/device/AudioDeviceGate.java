package moodss.ia.sfx.api.device;

import moodss.ia.sfx.api.AudioException;

public interface AudioDeviceGate {
    void apply(AudioDeviceContext context) throws AudioException;
}
