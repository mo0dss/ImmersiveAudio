package moodss.ia.sfx.openal;

import it.unimi.dsi.fastutil.HashCommon;

/**
 * An abstract object used to represent objects in OpenAL code safely. This class hides the direct handle to a OpenAL
 * object, requiring that it first be checked by all callers to prevent null pointer de-referencing. However, this will
 * not stop code from cloning the handle and trying to use it after it has been deleted and as such should not be
 * relied on too heavily.
 */
public abstract class AlObject {
    private static final int INVALID_HANDLE = Integer.MIN_VALUE;

    private int handle = INVALID_HANDLE;

    protected AlObject() {

    }

    protected final void setHandle(int handle) {
        if (handle == INVALID_HANDLE) {
            throw new IllegalArgumentException("Handle must be valid");
        }

        this.handle = handle;
    }

    public final int getHandle() {
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
        AlObject alObject = (AlObject) o;
        return this.handle == alObject.handle;
    }

    @Override
    public int hashCode() {
        return HashCommon.mix(this.handle);
    }
}
