package moodss.ia.user;

import com.google.gson.*;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class SupportedSoundType {

    //TODO: Required?
    protected static final AtomicInteger nextId = new AtomicInteger(0);

    public static SupportedSoundType fromJson(JsonElement json) {
        return new SupportedSoundType(json.getAsString());
    }

    public static JsonDeserializer<SupportedSoundType> jsonDeserializer() {
        return (json, type, ctx) -> fromJson(json);
    }

    private final int id;
    private final String name;

    protected SupportedSoundType(String name) {
        this.id = nextId.incrementAndGet();
        this.name = name;
    }

    public static SupportedSoundType create(String name) {
        return new SupportedSoundType(name);
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        SupportedSoundType that = (SupportedSoundType) o;

        if (this.id != that.id) return false;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        int result = this.id;
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        return result;
    }
}
