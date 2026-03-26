package net.minecraftforge.fml.javafmlmod;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import java.util.function.Supplier;

public final class FMLJavaModLoadingContext {

    public <T> void registerExtensionPoint(final Class<?> point, final Supplier<T> extension) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public BusGroup getModBusGroup() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
