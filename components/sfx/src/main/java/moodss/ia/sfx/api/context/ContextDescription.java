package moodss.ia.sfx.api.context;

import moodss.ia.sfx.api.types.ALCToken;

public record ContextDescription(ContextBinding[] bindings) {

    public record ContextBinding(ALCToken token, int value) {

    }
}
