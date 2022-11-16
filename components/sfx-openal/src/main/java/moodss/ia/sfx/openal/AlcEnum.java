package moodss.ia.sfx.openal;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import moodss.ia.sfx.api.types.ALCToken;
import org.lwjgl.openal.*;

import java.util.function.Consumer;

public class AlcEnum {

    private static final int[] ALC_TOKENS = build(ALCToken.class, (map) -> {
        map.put(ALCToken.MONO,                    ALC11.ALC_MONO_SOURCES);
        map.put(ALCToken.STEREO,                  ALC11.ALC_STEREO_SOURCES);

        map.put(ALCToken.MINOR_VERSION,           EXTEfx.ALC_EFX_MINOR_VERSION);
        map.put(ALCToken.MAJOR_VERSION,           EXTEfx.ALC_EFX_MAJOR_VERSION);
        map.put(ALCToken.MAX_AUXILIARY_SENDS,     EXTEfx.ALC_MAX_AUXILIARY_SENDS);

        map.put(ALCToken.HRTF,                    SOFTHRTF.ALC_HRTF_SOFT);
    });

    public static int from(ALCToken token) {
        return ALC_TOKENS[token.ordinal()];
    }

    protected static <T extends Enum<T>> int[] build(Class<T> type, Consumer<Reference2IntMap<T>> consumer) {
        Enum<T>[] universe = type.getEnumConstants();

        Reference2IntMap<T> map = new Reference2IntOpenHashMap<>(universe.length);
        map.defaultReturnValue(-1);

        consumer.accept(map);

        int[] values = new int[universe.length];

        for (Enum<T> e : universe) {
            int value = map.getInt(e);

            if (value == -1) {
                throw new RuntimeException("No mapping defined for " + e.name());
            }

            values[e.ordinal()] = value;
        }

        return values;
    }
}
