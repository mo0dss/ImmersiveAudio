package moodss.ia.sfx.openal;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import moodss.ia.sfx.api.types.ErrorCondition;

import java.util.function.Consumer;

public class AlTags {
    public static final String[] ERROR_CODE_NAMES = build(ErrorCondition.class, (map) -> {
        map.put(ErrorCondition.NONE,                            "AL_NO_ERROR");
        map.put(ErrorCondition.INVALID_NAME,                    "AL_INVALID_NAME");
        map.put(ErrorCondition.INVALID_VALUE,                   "AL_INVALID_VALUE");
        map.put(ErrorCondition.INVALID_OPERATION,               "AL_INVALID_OPERATION");
        map.put(ErrorCondition.OUT_OF_MEMORY,                   "AL_OUT_OF_MEMORY");
    });

    public static String from(ErrorCondition condition) {
        return ERROR_CODE_NAMES[condition.ordinal()];
    }

    protected static <T extends Enum<T>> String[] build(Class<T> type, Consumer<Reference2ObjectMap<T, String>> consumer) {
        Enum<T>[] universe = type.getEnumConstants();

        Reference2ObjectMap<T, String> map = new Reference2ObjectOpenHashMap<>(universe.length);
        map.defaultReturnValue("");

        consumer.accept(map);

        String[] values = new String[universe.length];

        for (Enum<T> e : universe) {
            String value = map.get(e);

            if (value.isEmpty()) {
                throw new RuntimeException("No mapping defined for " + e.name());
            }

            values[e.ordinal()] = value;
        }

        return values;
    }
}
