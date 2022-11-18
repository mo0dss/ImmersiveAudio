package moodss.ia.sfx.openal.filter;

import moodss.ia.sfx.api.effects.filter.Filter;
import moodss.ia.sfx.api.effects.types.FilterType;
import moodss.ia.sfx.openal.AlObject;
import moodss.ia.sfx.openal.EfxEnum;
import org.lwjgl.openal.EXTEfx;

public class AlEfxFilter extends AlObject implements Filter {

    private final FilterType type;

    public AlEfxFilter(FilterType type) {
        this.type = type;

        var handle = EXTEfx.alGenFilters();
        this.setHandle(handle);

        EXTEfx.alFilteri(handle, EXTEfx.AL_FILTER_TYPE, EfxEnum.from(type));
    }

    @Override
    public FilterType type() {
        return this.type;
    }
}
