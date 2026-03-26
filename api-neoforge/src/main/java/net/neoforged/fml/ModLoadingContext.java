package net.neoforged.fml;

import java.util.function.Supplier;

public final class ModLoadingContext {

    public static ModLoadingContext get() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public <T> void registerExtensionPoint(final Class<T> point, final Supplier<T> extension) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
