package moodss.ia.sfx.openal;

import it.unimi.dsi.fastutil.HashCommon;

/**
 * An abstract object used to represent objects in OpenALC code safely. This class hides the direct handle to a OpenALC
 * object, requiring that it first be checked by all callers to prevent null pointer de-referencing. However, this will
 * not stop code from cloning the handle and trying to use it after it has been deleted and as such should not be
 * relied on too heavily.
 */
public abstract class AlcObject {
    private static final long INVALID_HANDLE = Long.MIN_VALUE;

    private long handle = INVALID_HANDLE;

    protected AlcObject() {

    }

    protected final void setHandle(long handle) {
        if (handle == INVALID_HANDLE) {
            throw new IllegalArgumentException("Handle must be valid");
        }

        this.handle = handle;
    }

    public final long getHandle() {
        if (this.handle == INVALID_HANDLE) {
            throw new IllegalStateException("%s handle is not valid"
                    .formatted(this.getClass().getSimpleName()));
        }

        return this.handle;
    }

    public final void invalidateHandle() {
        this.handle = INVALID_HANDLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        AlcObject alcObject = (AlcObject) o;
        return this.handle == alcObject.handle;
    }

    @Override
    public int hashCode() {
        return (int) HashCommon.mix(this.handle);
    }
}
