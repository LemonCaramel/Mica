package moe.caramel.mica;

import static net.minecraft.network.chat.Component.translatable;
import moe.caramel.mica.screen.ModConfigScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

/**
 * Mica Instance
 */
public final class Mica {

    public static final String MOD_ID = "mica";
    public static final String MOD_NAME = "Mica";
    private static final String URL_YACL_DOWNLOAD = "https://modrinth.com/mod/yacl";

    public static final int MINIMUM_BUILD_NUM = 22000;
    public static final int BACKDROP_BUILD_NUM = 22621;

    public static int majorVersion = Integer.MIN_VALUE;
    public static int buildNumber = Integer.MIN_VALUE;

    /**
     * Check compatibility.
     *
     * @return if {@code true}, it is compatible.
     */
    public static boolean checkCompatibility() {
        return (Mica.majorVersion >= 10 && Mica.buildNumber >= Mica.MINIMUM_BUILD_NUM);
    }

    /**
     * Create a config screen.
     *
     * @param screen previous screen
     * @return config screen
     */
    @NotNull
    public static Screen configScreen(final Screen screen) {
        // Check OS compatibility
        if (!Mica.checkCompatibility()) {
            return new AlertScreen(
                () -> Minecraft.getInstance().setScreen(screen),
                translatable("mica.unsupported_os.title").withStyle(ChatFormatting.BOLD, ChatFormatting.RED),
                translatable("mica.unsupported_os.description", Mica.MOD_NAME)
            );
        }

        // Create Mod Config screen
        try {
            return ModConfigScreen.create(screen);
        } catch (final NoClassDefFoundError ignored) {
            return new ConfirmScreen(
                accept -> {
                    if (accept) {
                        final Minecraft client = Minecraft.getInstance();
                        client.setScreen(new ConfirmLinkScreen(confirm -> {
                            if (confirm) {
                                Util.getPlatform().openUri(URL_YACL_DOWNLOAD);
                            }

                            client.setScreen(screen);
                        }, URL_YACL_DOWNLOAD, true));
                    } else {
                        Minecraft.getInstance().setScreen(screen);
                    }
                },
                translatable("mica.missing_yacl.title").withStyle(ChatFormatting.BOLD, ChatFormatting.RED),
                translatable("mica.missing_yacl.description", Mica.MOD_NAME),
                translatable("mica.missing_yacl.download"),
                translatable("gui.back")
            );
        }
    }
}
