package moe.caramel.mica;

import com.mojang.blaze3d.platform.Window;
import moe.caramel.mica.natives.DwmApi;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.dedicated.Settings;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.nio.file.Path;
import java.util.Properties;

public final class ModConfig extends Settings<ModConfig> {

    private static final Path MOD_CONFIG = new File("./config/caramel.mica.properties").toPath();
    private static ModConfig instance;

    @NotNull
    public static ModConfig get() {
        if (instance == null) {
            instance = new ModConfig();
        }

        return instance;
    }

    /* ======================================== */

    public final Settings<ModConfig>.MutableValue<Boolean> useImmersiveDarkMode;
    public final Settings<ModConfig>.MutableValue<DwmApi.DWM_SYSTEMBACKDROP_TYPE> systemBackdropType;
    public final Settings<ModConfig>.MutableValue<DwmApi.DWM_WINDOW_CORNER_PREFERENCE> windowCorner;

    public final Settings<ModConfig>.MutableValue<Boolean> useDefaultBorder;
    public final Settings<ModConfig>.MutableValue<Boolean> hideWindowBorder;
    public final Settings<ModConfig>.MutableValue<Integer> borderColor;

    public final Settings<ModConfig>.MutableValue<Boolean> useDefaultCaption;
    public final Settings<ModConfig>.MutableValue<Integer> captionColor;

    public final Settings<ModConfig>.MutableValue<Boolean> useDefaultText;
    public final Settings<ModConfig>.MutableValue<Integer> textColor;


    private ModConfig() {
        this(Settings.loadFromFile(MOD_CONFIG));
    }

    private ModConfig(final Properties properties) {
        super(properties);
        // DWMWA_USE_IMMERSIVE_DARK_MODE
        this.useImmersiveDarkMode = this.getMutable("use-immersive-dark-mode", Boolean::parseBoolean, false);
        // DWMWA_SYSTEMBACKDROP_TYPE
        this.systemBackdropType = this.getMutable("system-backdrop-type", value -> {
            try { return DwmApi.DWM_SYSTEMBACKDROP_TYPE.valueOf(value); }
            catch (final IllegalArgumentException ignored) {
                return DwmApi.DWM_SYSTEMBACKDROP_TYPE.DWMSBT_AUTO;
            }
        }, DwmApi.DWM_SYSTEMBACKDROP_TYPE.DWMSBT_AUTO);
        // DWMWA_WINDOW_CORNER_PREFERENCE
        this.windowCorner = this.getMutable("window-corner-preference", value -> {
            try { return DwmApi.DWM_WINDOW_CORNER_PREFERENCE.valueOf(value); }
            catch (final IllegalArgumentException ignored) {
                return DwmApi.DWM_WINDOW_CORNER_PREFERENCE.DWMWCP_DEFAULT;
            }
        }, DwmApi.DWM_WINDOW_CORNER_PREFERENCE.DWMWCP_DEFAULT);
        // DWMWA_BORDER_COLOR
        this.useDefaultBorder = this.getMutable("use-default-border-color", Boolean::parseBoolean, true);
        this.hideWindowBorder = this.getMutable("hide-window-border", Boolean::parseBoolean, false);
        this.borderColor = this.getMutable("border-color", value -> {
            try { return Integer.parseInt(value); }
            catch (final NumberFormatException ignored) {
                return DwmApi.DWMWA_COLOR_DEFAULT;
            }
        }, DwmApi.DWMWA_COLOR_DEFAULT);
        // DWMWA_CAPTION_COLOR
        this.useDefaultCaption = this.getMutable("use-default-caption-color", Boolean::parseBoolean, true);
        this.captionColor = this.getMutable("caption-color", value -> {
            try { return Integer.parseInt(value); }
            catch (final NumberFormatException ignored) {
                return DwmApi.DWMWA_COLOR_DEFAULT;
            }
        }, DwmApi.DWMWA_COLOR_DEFAULT);
        // DWMWA_TEXT_COLOR
        this.useDefaultText = this.getMutable("use-default-text-color", Boolean::parseBoolean, true);
        this.textColor = this.getMutable("text-color", value -> {
            try { return Integer.parseInt(value); }
            catch (final NumberFormatException ignored) {
                return DwmApi.DWMWA_COLOR_DEFAULT;
            }
        }, DwmApi.DWMWA_COLOR_DEFAULT);
    }

    /* ======================================== */

    @Override
    protected @NotNull ModConfig reload(RegistryAccess registryAccess, Properties properties) {
        instance = new ModConfig(properties);
        instance.store(MOD_CONFIG);
        return get();
    }

    public <T> void setConfig(Settings<ModConfig>.MutableValue<T> config, T value) {
        config.update(null, value);
        ModConfig.update();
    }

    public static void update() {
        final Window window = Minecraft.getInstance().getWindow();
        DwmApi.updateDwm(window.isFullscreen(), window.handle());
    }
}
