package moe.caramel.mica.neoforge;

import moe.caramel.mica.Mica;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;

@Mod(Mica.MOD_ID)
public final class MicaNeoForge {

    public MicaNeoForge() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> new IConfigScreenFactory() {

            // ~ 1.20.6
            @NotNull
            public Screen createScreen(final @NotNull Minecraft minecraft, final @NotNull Screen screen) {
                return Mica.configScreen(screen);
            }

            // 1.21 ~
            @Override
            public @NotNull Screen createScreen(final @NotNull ModContainer container, final @NotNull Screen screen) {
                return Mica.configScreen(screen);
            }
        });
    }
}
