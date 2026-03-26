package moe.caramel.mica.screen;

import static net.minecraft.network.chat.Component.translatable;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.StateManager;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import moe.caramel.mica.Mica;
import moe.caramel.mica.ModConfig;
import moe.caramel.mica.natives.DwmApi;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;

/**
 * Mod Config screen
 */
public final class ModConfigScreen {

    @NotNull
    public static Screen create(final Screen screen) {
        return YetAnotherConfigLib.createBuilder()
            .title(Component.literal(Mica.MOD_NAME))
            .category(categoryGeneral())
            .category(categoryBorder())
            .category(categoryCaption())
            .category(categoryText())
            .build().generateScreen(screen);
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
                .stateManager(StateManager.createInstant(
                    false,
                    () -> ModConfig.get().useImmersiveDarkMode.get(),
                    value -> {
                        final ModConfig config = ModConfig.get();
                        config.setConfig(config.useImmersiveDarkMode, value);
                    }
                ))
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
                .controller(option -> EnumControllerBuilder.create(option).enumClass(DwmApi.DWM_SYSTEMBACKDROP_TYPE.class).formatValue(type -> {
                    return translatable("mica.category.general.system_backdrop_type.type." + type.translate);
                }))
                .stateManager(StateManager.createInstant(
                    DwmApi.DWM_SYSTEMBACKDROP_TYPE.DWMSBT_AUTO,
                    () -> ModConfig.get().systemBackdropType.get(),
                    value -> {
                        final ModConfig config = ModConfig.get();
                        config.setConfig(config.systemBackdropType, value);
                    }
                ))
                .build()
            )

            // Corner Preference
            .option(Option.<DwmApi.DWM_WINDOW_CORNER_PREFERENCE>createBuilder()
                .name(translatable("mica.category.general.corner_preference"))
                .description(OptionDescription.of(translatable("mica.category.general.corner_preference.description")))
                .controller(option -> EnumControllerBuilder.create(option).enumClass(DwmApi.DWM_WINDOW_CORNER_PREFERENCE.class).formatValue(type -> {
                    return translatable("mica.category.general.corner_preference.type." + type.translate);
                }))
                .stateManager(StateManager.createInstant(
                    DwmApi.DWM_WINDOW_CORNER_PREFERENCE.DWMWCP_DEFAULT,
                    () -> ModConfig.get().windowCorner.get(),
                    value -> {
                        final ModConfig config = ModConfig.get();
                        config.setConfig(config.windowCorner, value);
                    }
                ))
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
            .stateManager(StateManager.createInstant(
                new Color(DwmApi.DWMWA_COLOR_DEFAULT),
                () -> new Color(ModConfig.get().borderColor.get()),
                value -> {
                    final ModConfig config = ModConfig.get();
                    config.setConfig(config.borderColor, value.getRGB());
                }
            ))
            .build();
        final Option<Boolean> hideWindowBorder = Option.<Boolean>createBuilder()
            .name(translatable("mica.category.border.hide_border"))
            .description(OptionDescription.of(translatable("mica.category.border.hide_border.description")))
            .available(!ModConfig.get().useDefaultBorder.get())
            .controller(BooleanControllerBuilder::create)
            .stateManager(StateManager.createInstant(
                false,
                () -> ModConfig.get().hideWindowBorder.get(),
                value -> {
                    final ModConfig config = ModConfig.get();
                    config.setConfig(config.hideWindowBorder, value);
                    borderColor.setAvailable(!value);
                }
            ))
            .build();

        return ConfigCategory.createBuilder()
            .name(translatable("mica.category.border"))
            .option(Option.<Boolean>createBuilder()
                .name(translatable("mica.category.border.use_default_border"))
                .description(OptionDescription.of(translatable("mica.category.border.use_default_border.description")))
                .controller(BooleanControllerBuilder::create)
                .stateManager(StateManager.createInstant(
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
                ))
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
            .stateManager(StateManager.createInstant(
                new Color(DwmApi.DWMWA_COLOR_DEFAULT),
                () -> new Color(ModConfig.get().captionColor.get()),
                value -> {
                    final ModConfig config = ModConfig.get();
                    config.setConfig(config.captionColor, value.getRGB());
                }
            ))
            .build();

        return ConfigCategory.createBuilder()
            .name(translatable("mica.category.caption"))
            .option(Option.<Boolean>createBuilder()
                .name(translatable("mica.category.caption.use_default_caption"))
                .description(OptionDescription.of(translatable("mica.category.caption.use_default_caption.description")))
                .controller(BooleanControllerBuilder::create)
                .stateManager(StateManager.createInstant(
                    true,
                    () -> ModConfig.get().useDefaultCaption.get(),
                    value -> {
                        final ModConfig config = ModConfig.get();
                        config.setConfig(config.useDefaultCaption, value);
                        captionColor.setAvailable(!value);
                    }
                ))
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
            .stateManager(StateManager.createInstant(
                new Color(DwmApi.DWMWA_COLOR_DEFAULT),
                () -> new Color(ModConfig.get().textColor.get()),
                value -> {
                    final ModConfig config = ModConfig.get();
                    config.setConfig(config.textColor, value.getRGB());
                }
            ))
            .build();

        return ConfigCategory.createBuilder()
            .name(translatable("mica.category.title_text"))
            .option(Option.<Boolean>createBuilder()
                .name(translatable("mica.category.title_text.use_default_color"))
                .description(OptionDescription.of(translatable("mica.category.title_text.use_default_color.description")))
                .controller(BooleanControllerBuilder::create)
                .stateManager(StateManager.createInstant(
                    true,
                    () -> ModConfig.get().useDefaultText.get(),
                    value -> {
                        final ModConfig config = ModConfig.get();
                        config.setConfig(config.useDefaultText, value);
                        textColor.setAvailable(!value);
                    }
                ))
                .build()
            )
            .option(textColor)
            .build();
    }
}
