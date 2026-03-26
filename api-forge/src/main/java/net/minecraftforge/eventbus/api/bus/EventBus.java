package net.minecraftforge.eventbus.api.bus;

import java.util.function.Consumer;

public interface EventBus<T> {

    void addListener(Consumer<T> listener);
}
