package moodss.ia.sfx.openal.source;

import moodss.ia.sfx.api.source.Source;
import moodss.ia.sfx.openal.AlObject;
import org.lwjgl.openal.AL10;

public class AlSource extends AlObject implements Source {

    public AlSource(int handle) {
        this.setHandle(handle);
    }

    public static AlSource wrap(int handle) {
        if(!AL10.alIsSource(handle)) {
            throw new IllegalArgumentException("Handle provided is not an OpenAL source name.");
        }

        return new AlSource(handle);
    }

    public static int getHandle(Source source) {
        return ((AlSource) source).getHandle();
    }
}
