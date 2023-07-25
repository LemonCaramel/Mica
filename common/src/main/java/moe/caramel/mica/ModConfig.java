package moe.caramel.mica;

import static net.minecraft.network.chat.Component.translatable;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import moe.caramel.mica.natives.DwmApi;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.Settings;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
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
        DwmApi.updateDwm(Minecraft.getInstance().getWindow().getWindow());
    }

    /* ======================================== */

    @NotNull
    public static Screen create(final Screen screen) {
        if (Mica.buildNumber < Mica.MINIMUM_BUILD_NUM) {
            return new AlertScreen(
                () -> Minecraft.getInstance().setScreen(screen),
                translatable("mica.unsupported_os.title").withStyle(ChatFormatting.BOLD, ChatFormatting.RED),
                translatable("mica.unsupported_os.description", Mica.MOD_NAME)
            );
        }

        try {
            return YetAnotherConfigLib.createBuilder()
                .title(Component.literal(Mica.MOD_NAME))
                .category(categoryGeneral())
                .category(categoryBorder())
                .category(categoryCaption())
                .category(categoryText())
                .build().generateScreen(screen);
        } catch (final NoClassDefFoundError ignored) {
            return new ConfirmScreen(
                accept -> {
                    if (accept) {
                        ConfirmLinkScreen.confirmLinkNow("https://modrinth.com/mod/yacl", screen, true);
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

    @NotNull
    private static ConfigCategory categoryGeneral() {
        return ConfigCategory.createBuilder()
            .name(translatable("mica.category.general"))

            // Use Immersive Dark Mode
            .option(Option.<Boolean>createBuilder()
                .name(translatable("mica.category.general.use_immersive_dark_mode"))
                .description(OptionDescription.of(translatable("mica.category.general.use_immersive_dark_mode.description")))
                .controller(BooleanControllerBuilder::create)
                .binding(
                    false,
                    () -> ModConfig.get().useImmersiveDarkMode.get(),
                    value -> {
                        final ModConfig config = ModConfig.get();
                        config.setConfig(config.useImmersiveDarkMode, value);
                    }
                )
                .instant(true)
                .build()
            )

            // System Backdrop Type
            .option(Option.<DwmApi.DWM_SYSTEMBACKDROP_TYPE>createBuilder()
                .name(translatable("mica.category.general.system_backdrop_type"))
                .description(OptionDescription.of(
                    translatable("mica.category.general.system_backdrop_type.description"),
                    Component.literal("\n"),
                    translatable("mica.category.general.system_backdrop_type.description2").withStyle(ChatFormatting.GRAY)
                ))
                .available(Mica.buildNumber >= Mica.BACKDROP_BUILD_NUM) // >= 22621
                .controller(option -> EnumControllerBuilder.create(option).enumClass(DwmApi.DWM_SYSTEMBACKDROP_TYPE.class).valueFormatter(type -> {
                    return translatable("mica.category.general.system_backdrop_type.type." + type.translate);
                }))
                .binding(
                    DwmApi.DWM_SYSTEMBACKDROP_TYPE.DWMSBT_AUTO,
                    () -> ModConfig.get().systemBackdropType.get(),
                    value -> {
                        final ModConfig config = ModConfig.get();
                        config.setConfig(config.systemBackdropType, value);
                    }
                )
                .instant(true)
                .build()
            )

            // Corner Preference
            .option(Option.<DwmApi.DWM_WINDOW_CORNER_PREFERENCE>createBuilder()
                .name(translatable("mica.category.general.corner_preference"))
                .description(OptionDescription.of(translatable("mica.category.general.corner_preference.description")))
                .controller(option -> EnumControllerBuilder.create(option).enumClass(DwmApi.DWM_WINDOW_CORNER_PREFERENCE.class).valueFormatter(type -> {
                    return translatable("mica.category.general.corner_preference.type." + type.translate);
                }))
                .binding(
                    DwmApi.DWM_WINDOW_CORNER_PREFERENCE.DWMWCP_DEFAULT,
                    () -> ModConfig.get().windowCorner.get(),
                    value -> {
                        final ModConfig config = ModConfig.get();
                        config.setConfig(config.windowCorner, value);
                    }
                )
                .instant(true)
                .build()
            )
            .build();
    }

    @NotNull
    private static ConfigCategory categoryBorder() {
        final Option<Color> borderColor = Option.<Color>createBuilder()
            .name(translatable("mica.category.border.border_color"))
            .description(OptionDescription.of(translatable("mica.category.border.border_color.description")))
            .available(!ModConfig.get().useDefaultBorder.get() && !ModConfig.get().hideWindowBorder.get())
            .controller(ColorControllerBuilder::create)
            .binding(
                new Color(DwmApi.DWMWA_COLOR_DEFAULT),
                () -> new Color(ModConfig.get().borderColor.get()),
                value -> {
                    final ModConfig config = ModConfig.get();
                    config.setConfig(config.borderColor, value.getRGB());
                }
            )
            .instant(true)
            .build();
        final Option<Boolean> hideWindowBorder = Option.<Boolean>createBuilder()
            .name(translatable("mica.category.border.hide_border"))
            .description(OptionDescription.of(translatable("mica.category.border.hide_border.description")))
            .available(!ModConfig.get().useDefaultBorder.get())
            .controller(BooleanControllerBuilder::create)
            .binding(
                false,
                () -> ModConfig.get().hideWindowBorder.get(),
                value -> {
                    final ModConfig config = ModConfig.get();
                    config.setConfig(config.hideWindowBorder, value);
                    borderColor.setAvailable(!value);
                }
            )
            .instant(true)
            .build();

        return ConfigCategory.createBuilder()
            .name(translatable("mica.category.border"))
            .option(Option.<Boolean>createBuilder()
                .name(translatable("mica.category.border.use_default_border"))
                .description(OptionDescription.of(translatable("mica.category.border.use_default_border.description")))
                .controller(BooleanControllerBuilder::create)
                .binding(
                    true,
                    () -> ModConfig.get().useDefaultBorder.get(),
                    value -> {
                        final ModConfig config = ModConfig.get();
                        config.setConfig(config.useDefaultBorder, value);

                        final boolean lock = !value;
                        hideWindowBorder.setAvailable(lock);
                        borderColor.setAvailable(lock);
                        if (lock) {
                            final boolean enabled = config.hideWindowBorder.get();
                            borderColor.setAvailable(!enabled);
                        }
                    }
                )
                .instant(true)
                .build()
            )
            .option(hideWindowBorder)
            .option(borderColor)
            .build();
    }

    @NotNull
    private static ConfigCategory categoryCaption() {
        final Option<Color> captionColor = Option.<Color>createBuilder()
            .name(translatable("mica.category.caption.caption_color"))
            .description(OptionDescription.of(translatable("mica.category.caption.caption_color.description")))
            .available(!ModConfig.get().useDefaultCaption.get())
            .controller(ColorControllerBuilder::create)
            .binding(
                new Color(DwmApi.DWMWA_COLOR_DEFAULT),
                () -> new Color(ModConfig.get().captionColor.get()),
                value -> {
                    final ModConfig config = ModConfig.get();
                    config.setConfig(config.captionColor, value.getRGB());
                }
            )
            .instant(true)
            .build();

        return ConfigCategory.createBuilder()
            .name(translatable("mica.category.caption"))
            .option(Option.<Boolean>createBuilder()
                .name(translatable("mica.category.caption.use_default_caption"))
                .description(OptionDescription.of(translatable("mica.category.caption.use_default_caption.description")))
                .controller(BooleanControllerBuilder::create)
                .binding(
                    true,
                    () -> ModConfig.get().useDefaultCaption.get(),
                    value -> {
                        final ModConfig config = ModConfig.get();
                        config.setConfig(config.useDefaultCaption, value);
                        captionColor.setAvailable(!value);
                    }
                )
                .instant(true)
                .build()
            )
            .option(captionColor)
            .build();
    }

    @NotNull
    private static ConfigCategory categoryText() {
        final Option<Color> textColor = Option.<Color>createBuilder()
            .name(translatable("mica.category.title_text.text_color"))
            .description(OptionDescription.of(translatable("mica.category.title_text.text_color.description")))
            .available(!ModConfig.get().useDefaultText.get())
            .controller(ColorControllerBuilder::create)
            .binding(
                new Color(DwmApi.DWMWA_COLOR_DEFAULT),
                () -> new Color(ModConfig.get().textColor.get()),
                value -> {
                    final ModConfig config = ModConfig.get();
                    config.setConfig(config.textColor, value.getRGB());
                }
            )
            .instant(true)
            .build();

        return ConfigCategory.createBuilder()
            .name(translatable("mica.category.title_text"))
            .option(Option.<Boolean>createBuilder()
                .name(translatable("mica.category.title_text.use_default_color"))
                .description(OptionDescription.of(translatable("mica.category.title_text.use_default_color.description")))
                .controller(BooleanControllerBuilder::create)
                .binding(
                    true,
                    () -> ModConfig.get().useDefaultText.get(),
                    value -> {
                        final ModConfig config = ModConfig.get();
                        config.setConfig(config.useDefaultText, value);
                        textColor.setAvailable(!value);
                    }
                )
                .instant(true)
                .build()
            )
            .option(textColor)
            .build();
    }
}
