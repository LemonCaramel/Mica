package moe.caramel.mica.neoforge;

import moe.caramel.mica.Mica;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.client.ConfigScreenHandler;

@Mod(Mica.MOD_ID)
public final class MicaNeoForge {

    public MicaNeoForge() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> {
            return new ConfigScreenHandler.ConfigScreenFactory((client, screen) -> Mica.configScreen(screen));
        });
    }
}
