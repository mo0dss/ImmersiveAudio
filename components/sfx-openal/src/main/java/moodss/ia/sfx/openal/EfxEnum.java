package moodss.ia.sfx.openal;

import moodss.ia.sfx.api.types.*;
import org.lwjgl.openal.EXTEfx;

public class EfxEnum extends AlEnum {

    private static final int[] LOWPASS_FILTER_TYPES = build(LowpassFilterType.class, (map) -> {
        map.put(LowpassFilterType.GAIN,                     EXTEfx.AL_LOWPASS_GAIN);
        map.put(LowpassFilterType.GAIN_HF,                  EXTEfx.AL_LOWPASS_GAINHF);
    });

    private static final int[] HIGHPASS_FILTER_TYPES = build(HighpassFilterType.class, (map) -> {
        map.put(HighpassFilterType.GAIN,                     EXTEfx.AL_HIGHPASS_GAIN);
        map.put(HighpassFilterType.GAIN_LF,                  EXTEfx.AL_HIGHPASS_GAINLF);
    });

    private static final int[] BANDPASS_FILTER_TYPES = build(BandpassFilterType.class, (map) -> {
       map.put(BandpassFilterType.GAIN,                      EXTEfx.AL_BANDPASS_GAIN);
       map.put(BandpassFilterType.GAIN_LF,                   EXTEfx.AL_BANDPASS_GAINLF);
       map.put(BandpassFilterType.GAIN_HF,                   EXTEfx.AL_BANDPASS_GAINHF);
    });

    private static final int[] FILTER_TYPES = build(FilterType.class, (map) -> {
        map.put(FilterType.LOWPASS,                 EXTEfx.AL_FILTER_LOWPASS);
        map.put(FilterType.HIGHPASS,                EXTEfx.AL_FILTER_HIGHPASS);
        map.put(FilterType.BANDPASS,                EXTEfx.AL_FILTER_BANDPASS);
        map.put(FilterType.ALL,                     EXTEfx.AL_FILTER_NULL);
    });

    private static final int[] EFFECT_TYPES = build(EffectType.class, (map) -> {
        map.put(EffectType.ALL,                     EXTEfx.AL_EFFECT_NULL);
        map.put(EffectType.REVERB,                  EXTEfx.AL_EFFECT_REVERB);
        map.put(EffectType.CHORUS,                  EXTEfx.AL_EFFECT_CHORUS);
        map.put(EffectType.DISTORATION,             EXTEfx.AL_EFFECT_DISTORTION);
        map.put(EffectType.ECHO,                    EXTEfx.AL_EFFECT_ECHO);
        map.put(EffectType.FLANGER,                 EXTEfx.AL_EFFECT_FLANGER);
        map.put(EffectType.FREQUENCY_SHIFTER,       EXTEfx.AL_EFFECT_FREQUENCY_SHIFTER);
        map.put(EffectType.VOCAL_MORPHER,           EXTEfx.AL_EFFECT_VOCAL_MORPHER);
        map.put(EffectType.PITCH_SHIFTER,           EXTEfx.AL_EFFECT_PITCH_SHIFTER);
        map.put(EffectType.RING_MODULATOR,          EXTEfx.AL_EFFECT_RING_MODULATOR);
        map.put(EffectType.AUTOWAH,                 EXTEfx.AL_EFFECT_AUTOWAH);
        map.put(EffectType.COMPRESSOR,              EXTEfx.AL_EFFECT_COMPRESSOR);
        map.put(EffectType.EQUALIZER,               EXTEfx.AL_EFFECT_EQUALIZER);
        map.put(EffectType.EAXREVERB,               EXTEfx.AL_EFFECT_EAXREVERB);
    });

    private static final int[] AUXILIARY_EFFECT_PROPERTIES = build(AuxiliaryEffectProperty.class, (map) -> {
        map.put(AuxiliaryEffectProperty.EFFECT,                    EXTEfx.AL_EFFECTSLOT_EFFECT);
        map.put(AuxiliaryEffectProperty.GAIN,                      EXTEfx.AL_EFFECTSLOT_GAIN);
        map.put(AuxiliaryEffectProperty.SEND_AUTO,                 EXTEfx.AL_EFFECTSLOT_AUXILIARY_SEND_AUTO);
    });

    private static final int[] REVERB_PROPERTIES = build(ReverbProperties.class, (map) -> {
        map.put(ReverbProperties.DENSITY,                EXTEfx.AL_REVERB_DENSITY);
        map.put(ReverbProperties.DIFFUSION,              EXTEfx.AL_REVERB_DIFFUSION);
        map.put(ReverbProperties.GAIN,                   EXTEfx.AL_REVERB_GAIN);
        map.put(ReverbProperties.GAIN_HF,                EXTEfx.AL_REVERB_GAINHF);
        map.put(ReverbProperties.DECAY_TIME,             EXTEfx.AL_REVERB_DECAY_TIME);
        map.put(ReverbProperties.DECAY_HF_RATIO,         EXTEfx.AL_REVERB_DECAY_HFRATIO);
        map.put(ReverbProperties.REFLECTIONS_GAIN,       EXTEfx.AL_REVERB_REFLECTIONS_GAIN);
        map.put(ReverbProperties.REFLECTIONS_DELAY,      EXTEfx.AL_REVERB_REFLECTIONS_DELAY);
        map.put(ReverbProperties.LATE_GAIN,              EXTEfx.AL_REVERB_LATE_REVERB_GAIN);
        map.put(ReverbProperties.LATE_DELAY,             EXTEfx.AL_REVERB_LATE_REVERB_DELAY);
        map.put(ReverbProperties.AIR_ABSORPTION_GAIN_HF, EXTEfx.AL_REVERB_AIR_ABSORPTION_GAINHF);
        map.put(ReverbProperties.ROOM_ROLLOFF_FACTOR,    EXTEfx.AL_REVERB_ROOM_ROLLOFF_FACTOR);
        map.put(ReverbProperties.DECAY_HF_LIMIT,         EXTEfx.AL_REVERB_DECAY_HFLIMIT);
    });

    private static final int[] EAXREVERB_PROPERTIES = build(EAXReverbProperties.class, (map) -> {
        map.put(EAXReverbProperties.DENSITY,                EXTEfx.AL_EAXREVERB_DENSITY);
        map.put(EAXReverbProperties.DIFFUSION,              EXTEfx.AL_EAXREVERB_DIFFUSION);
        map.put(EAXReverbProperties.GAIN,                   EXTEfx.AL_EAXREVERB_GAIN);
        map.put(EAXReverbProperties.GAIN_HF,                EXTEfx.AL_EAXREVERB_GAINHF);
        map.put(EAXReverbProperties.GAIN_LF,                EXTEfx.AL_EAXREVERB_GAINLF);
        map.put(EAXReverbProperties.DECAY_TIME,             EXTEfx.AL_EAXREVERB_DECAY_TIME);
        map.put(EAXReverbProperties.DECAY_RATIO_HF,         EXTEfx.AL_EAXREVERB_DECAY_HFRATIO);
        map.put(EAXReverbProperties.DECAY_RATIO_LF,         EXTEfx.AL_EAXREVERB_DECAY_LFRATIO);
        map.put(EAXReverbProperties.REFLECTIONS_GAIN,       EXTEfx.AL_EAXREVERB_REFLECTIONS_GAIN);
        map.put(EAXReverbProperties.REFLECTIONS_DELAY,      EXTEfx.AL_EAXREVERB_REFLECTIONS_DELAY);
        map.put(EAXReverbProperties.REFLECTIONS_PAN,        EXTEfx.AL_EAXREVERB_REFLECTIONS_PAN);
        map.put(EAXReverbProperties.LATE_GAIN,              EXTEfx.AL_EAXREVERB_LATE_REVERB_GAIN);
        map.put(EAXReverbProperties.LATE_DELAY,             EXTEfx.AL_EAXREVERB_LATE_REVERB_DELAY);
        map.put(EAXReverbProperties.LATE_PAN,               EXTEfx.AL_EAXREVERB_LATE_REVERB_PAN);
        map.put(EAXReverbProperties.ECHO_TIME,              EXTEfx.AL_EAXREVERB_ECHO_TIME);
        map.put(EAXReverbProperties.ECHO_DEPTH,             EXTEfx.AL_EAXREVERB_ECHO_DEPTH);
        map.put(EAXReverbProperties.MODULATION_TIME,        EXTEfx.AL_EAXREVERB_MODULATION_TIME);
        map.put(EAXReverbProperties.MODULATION_DEPTH,       EXTEfx.AL_EAXREVERB_MODULATION_DEPTH);
        map.put(EAXReverbProperties.AIR_ABSORPTION_GAIN_HF, EXTEfx.AL_EAXREVERB_AIR_ABSORPTION_GAINHF);
        map.put(EAXReverbProperties.REFERENCE_HF,           EXTEfx.AL_EAXREVERB_HFREFERENCE);
        map.put(EAXReverbProperties.REFERENCE_LF,           EXTEfx.AL_EAXREVERB_LFREFERENCE);
        map.put(EAXReverbProperties.ROOM_ROLLOFF_FACTOR,    EXTEfx.AL_EAXREVERB_ROOM_ROLLOFF_FACTOR);
        map.put(EAXReverbProperties.DECAY_LIMIT_HF,         EXTEfx.AL_EAXREVERB_DECAY_HFLIMIT);
    });

    public static final int[] ECHO_PROPERTIES = build(EchoProperties.class, (map) -> {
        map.put(EchoProperties.DELAY,               EXTEfx.AL_ECHO_DELAY);
        map.put(EchoProperties.DELAY_LR,            EXTEfx.AL_ECHO_LRDELAY);
        map.put(EchoProperties.DAMPING,             EXTEfx.AL_ECHO_DAMPING);
        map.put(EchoProperties.FEEDBACK,            EXTEfx.AL_ECHO_FEEDBACK);
        map.put(EchoProperties.SPREAD,              EXTEfx.AL_ECHO_SPREAD);
    });

    public static int from(EchoProperties properties) {
        return ECHO_PROPERTIES[properties.ordinal()];
    }

    public static int from(EAXReverbProperties properties) {
        return EAXREVERB_PROPERTIES[properties.ordinal()];
    }

    public static int from(ReverbProperties properties) {
        return REVERB_PROPERTIES[properties.ordinal()];
    }

    public static int from(AuxiliaryEffectProperty property) {
        return AUXILIARY_EFFECT_PROPERTIES[property.ordinal()];
    }

    public static int from(EffectType type) {
        return EFFECT_TYPES[type.ordinal()];
    }

    public static int from(FilterType type) {
        return FILTER_TYPES[type.ordinal()];
    }

    public static int from(LowpassFilterType type) {
        return LOWPASS_FILTER_TYPES[type.ordinal()];
    }

    public static int from(HighpassFilterType type) {
        return HIGHPASS_FILTER_TYPES[type.ordinal()];
    }

    public static int from(BandpassFilterType type) {
        return BANDPASS_FILTER_TYPES[type.ordinal()];
    }

}
