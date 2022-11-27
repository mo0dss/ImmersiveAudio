package moodss.ia.sfx.openal;

import moodss.ia.sfx.api.types.SOFTBufferChannel;
import moodss.ia.sfx.api.types.SOFTBufferFormat;
import moodss.ia.sfx.api.types.SOFTBufferType;
import org.lwjgl.openal.SOFTBufferSamples;

public class AlSOFTEnum extends AlEnum {

    private static final int[] BUFFER_FORMATS = build(SOFTBufferFormat.class, (map) -> {
        map.put(SOFTBufferFormat.MONO8,               SOFTBufferSamples.AL_MONO8_SOFT);
        map.put(SOFTBufferFormat.MONO16,              SOFTBufferSamples.AL_MONO16_SOFT);
        map.put(SOFTBufferFormat.MONO32,              SOFTBufferSamples.AL_MONO32F_SOFT);

        map.put(SOFTBufferFormat.STEREO8,             SOFTBufferSamples.AL_STEREO8_SOFT);
        map.put(SOFTBufferFormat.STEREO16,            SOFTBufferSamples.AL_STEREO16_SOFT);
        map.put(SOFTBufferFormat.STEREO32,            SOFTBufferSamples.AL_STEREO32F_SOFT);

        map.put(SOFTBufferFormat.QUAD8,               SOFTBufferSamples.AL_QUAD8_SOFT);
        map.put(SOFTBufferFormat.QUAD16,              SOFTBufferSamples.AL_QUAD16_SOFT);
        map.put(SOFTBufferFormat.QUAD32,              SOFTBufferSamples.AL_QUAD32F_SOFT);

        map.put(SOFTBufferFormat.REAR8,               SOFTBufferSamples.AL_REAR8_SOFT);
        map.put(SOFTBufferFormat.REAR16,              SOFTBufferSamples.AL_REAR16_SOFT);
        map.put(SOFTBufferFormat.REAR32,              SOFTBufferSamples.AL_REAR32F_SOFT);

        map.put(SOFTBufferFormat.FIVE_ONE8,           SOFTBufferSamples.AL_5POINT1_8_SOFT);
        map.put(SOFTBufferFormat.FIVE_ONE16,          SOFTBufferSamples.AL_5POINT1_16_SOFT);
        map.put(SOFTBufferFormat.FIVE_ONE32,          SOFTBufferSamples.AL_5POINT1_32F_SOFT);

        map.put(SOFTBufferFormat.SIX_ONE8,            SOFTBufferSamples.AL_6POINT1_8_SOFT);
        map.put(SOFTBufferFormat.SIX_ONE16,           SOFTBufferSamples.AL_6POINT1_16_SOFT);
        map.put(SOFTBufferFormat.SIX_ONE32,           SOFTBufferSamples.AL_6POINT1_32F_SOFT);

        map.put(SOFTBufferFormat.SEVEN_ONE8,          SOFTBufferSamples.AL_7POINT1_8_SOFT);
        map.put(SOFTBufferFormat.SEVEN_ONE16,         SOFTBufferSamples.AL_7POINT1_16_SOFT);
        map.put(SOFTBufferFormat.SEVEN_ONE32,         SOFTBufferSamples.AL_7POINT1_32F_SOFT);
    });

    private static final int[] BUFFER_CHANNELS = build(SOFTBufferChannel.class, (map) -> {
        map.put(SOFTBufferChannel.MONO,                SOFTBufferSamples.AL_MONO_SOFT);
        map.put(SOFTBufferChannel.STEREO,              SOFTBufferSamples.AL_STEREO_SOFT);
        map.put(SOFTBufferChannel.QUAD,                SOFTBufferSamples.AL_QUAD_SOFT);
        map.put(SOFTBufferChannel.REAR,                SOFTBufferSamples.AL_REAR_SOFT);
        map.put(SOFTBufferChannel.FIVE_ONE,            SOFTBufferSamples.AL_5POINT1_SOFT);
        map.put(SOFTBufferChannel.SIX_ONE,             SOFTBufferSamples.AL_6POINT1_SOFT);
        map.put(SOFTBufferChannel.SEVEN_ONE,           SOFTBufferSamples.AL_7POINT1_SOFT);
    });

    private static final int[] BUFFER_TYPES = build(SOFTBufferType.class, (map) -> {
        map.put(SOFTBufferType.BYTE,                SOFTBufferSamples.AL_BYTE_SOFT);
        map.put(SOFTBufferType.UNSIGNED_BYTE,       SOFTBufferSamples.AL_UNSIGNED_BYTE_SOFT);
        map.put(SOFTBufferType.SHORT,               SOFTBufferSamples.AL_SHORT_SOFT);
        map.put(SOFTBufferType.UNSIGNED_SHORT,      SOFTBufferSamples.AL_UNSIGNED_SHORT_SOFT);
        map.put(SOFTBufferType.INT,                 SOFTBufferSamples.AL_INT_SOFT);
        map.put(SOFTBufferType.UNSIGNED_INT,        SOFTBufferSamples.AL_UNSIGNED_INT_SOFT);
        map.put(SOFTBufferType.FLOAT,               SOFTBufferSamples.AL_FLOAT_SOFT);
        map.put(SOFTBufferType.DOUBLE,              SOFTBufferSamples.AL_DOUBLE_SOFT);
        map.put(SOFTBufferType.BYTE3,               SOFTBufferSamples.AL_BYTE3_SOFT);
        map.put(SOFTBufferType.UNSIGNED_BYTE3,      SOFTBufferSamples.AL_UNSIGNED_BYTE3_SOFT);
    });

    public static int from(SOFTBufferFormat format) {
        return BUFFER_FORMATS[format.ordinal()];
    }

    public static int from(SOFTBufferChannel channel) {
        return BUFFER_CHANNELS[channel.ordinal()];
    }

    public static int from(SOFTBufferType type) {
        return BUFFER_TYPES[type.ordinal()];
    }
}
