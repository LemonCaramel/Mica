package net.minecraftforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class ConfigScreenHandler {

    public record ConfigScreenFactory(BiFunction<Minecraft, Screen, Screen> function) {

        public ConfigScreenFactory(final Function<Screen, Screen> function) {
            this((client, screen) -> function.apply(screen));
        }
    }
}
