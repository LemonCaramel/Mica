package moe.caramel.mica.platform.neoforge;

import moe.caramel.mica.Mica;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Mica.MOD_ID)
public final class MicaNeoForge {

    public MicaNeoForge() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () ->
            (_, screen) -> Mica.configScreen(screen));
    }
}
