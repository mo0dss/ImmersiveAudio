package moodss.ia.sfx.api.types;

public enum SOFTBufferFormat {

    MONO8, MONO16, MONO32,

    STEREO8, STEREO16, STEREO32,

    QUAD8, QUAD16, QUAD32,

    REAR8, REAR16, REAR32,

    FIVE_ONE8, FIVE_ONE16, FIVE_ONE32,

    SIX_ONE8, SIX_ONE16, SIX_ONE32,

    SEVEN_ONE8, SEVEN_ONE16, SEVEN_ONE32;

    public static final SOFTBufferFormat[] ALL = values();
}
