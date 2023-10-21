package moe.caramel.mica.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import moe.caramel.mica.Mica;
import net.minecraft.client.gui.screens.Screen;

public final class ModMenuImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<? extends Screen> getModConfigScreenFactory() {
        return Mica::configScreen;
    }
}
