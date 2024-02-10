package moe.caramel.mica.natives;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.IntByReference;
import moe.caramel.mica.Mica;
import moe.caramel.mica.ModConfig;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.NativeType;
import java.util.List;

public interface DwmApi extends Library {

    /* DWM API */
    DwmApi INSTANCE = Native.load("dwmapi", DwmApi.class);
    int INT_SIZE = 4;

    /* BOOL */
    int BOOL_FALSE = 0;
    int BOOL_TRUE = 1;

    /* DWMWINDOWATTRIBUTE */
    int DWMWA_USE_IMMERSIVE_DARK_MODE = 20;
    int DWMWA_WINDOW_CORNER_PREFERENCE = 33;
    int DWMWA_BORDER_COLOR = 34;
    int DWMWA_CAPTION_COLOR = 35;
    int DWMWA_TEXT_COLOR = 36;
    int DWMWA_SYSTEMBACKDROP_TYPE = 38;

    /* DWM_SYSTEMBACKDROP_TYPE */
    enum DWM_SYSTEMBACKDROP_TYPE {
        DWMSBT_AUTO("auto"), // 0 Auto
        DWMSBT_NONE("none"), // 1 None
        DWMSBT_MAINWINDOW("mica"), // 2 Mica
        DWMSBT_TRANSIENTWINDOW("acrylic"), // 3 Acrylic
        DWMSBT_TABBEDWINDOW("tabbed"); // 4 Tabbed

        public final String translate;

        DWM_SYSTEMBACKDROP_TYPE(final String translate) {
            this.translate = translate;
        }
    }

    /* DWM_WINDOW_CORNER_PREFERENCE */
    enum DWM_WINDOW_CORNER_PREFERENCE {
        DWMWCP_DEFAULT("default"), // 0
        DWMWCP_DONOTROUND("do_not_round"), // 1
        DWMWCP_ROUND("round"), // 2
        DWMWCP_ROUNDSMALL("round_small"); // 3

        public final String translate;

        DWM_WINDOW_CORNER_PREFERENCE(final String translate) {
            this.translate = translate;
        }
    }

    /* DWMWA_BORDER_COLOR_OPTION */
    int DWMWA_COLOR_NONE = 0xFFFFFFFE;
    int DWMWA_COLOR_DEFAULT = 0xFFFFFFFF;

    @NativeType("HRESULT") // Err
    int DwmSetWindowAttribute(
        HWND hwnd,
        int dwAttribute,
        PointerType pvAttribute,
        int cbAttribute
    );

    static void updateDwm(final boolean fullscreen, final long window) {
        // Check build number
        if (!Mica.checkCompatibility()) {
            return;
        }

        final HWND hwnd = new HWND(Pointer.createConstant(GLFWNativeWin32.glfwGetWin32Window(window)));
        if (fullscreen) {
            DwmApi.disableWindowEffect(hwnd);
            return;
        }

        final ModConfig config = ModConfig.get();

        // DWMWA_USE_IMMERSIVE_DARK_MODE
        final boolean useImmersiveDarkMode = config.useImmersiveDarkMode.get();
        INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_USE_IMMERSIVE_DARK_MODE, new IntByReference(useImmersiveDarkMode ? BOOL_TRUE : BOOL_FALSE), INT_SIZE);

        // DWMWA_SYSTEMBACKDROP_TYPE
        if (Mica.buildNumber >= Mica.BACKDROP_BUILD_NUM) {
            final DWM_SYSTEMBACKDROP_TYPE systemBackdropType = config.systemBackdropType.get();
            INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_SYSTEMBACKDROP_TYPE, new IntByReference(systemBackdropType.ordinal()), INT_SIZE);
        }

        // DWMWA_WINDOW_CORNER_PREFERENCE
        final DWM_WINDOW_CORNER_PREFERENCE windowCorner = config.windowCorner.get();
        INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_WINDOW_CORNER_PREFERENCE, new IntByReference(windowCorner.ordinal()), INT_SIZE);

        // DWMWA_BORDER_COLOR
        if (config.useDefaultBorder.get()) {
            INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_BORDER_COLOR, new IntByReference(DWMWA_COLOR_DEFAULT), INT_SIZE);
        } else if (config.hideWindowBorder.get()) {
            INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_BORDER_COLOR, new IntByReference(DWMWA_COLOR_NONE), INT_SIZE);
        } else {
            final int borderColor = convert(config.borderColor.get());
            INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_BORDER_COLOR, new IntByReference(borderColor), INT_SIZE);
        }

        // DWMWA_CAPTION_COLOR
        if (config.useDefaultCaption.get()) {
            INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_CAPTION_COLOR, new IntByReference(DWMWA_COLOR_DEFAULT), INT_SIZE);
        } else {
            final int captionColor = convert(config.captionColor.get());
            INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_CAPTION_COLOR, new IntByReference(captionColor), INT_SIZE);
        }

        // DWMWA_TEXT_COLOR
        if (config.useDefaultText.get()) {
            INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_TEXT_COLOR, new IntByReference(DWMWA_COLOR_DEFAULT), INT_SIZE);
        } else {
            final int textColor = convert(config.textColor.get());
            INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_TEXT_COLOR, new IntByReference(textColor), INT_SIZE);
        }
    }

    static void disableWindowEffect(final HWND hwnd) {
        // ... DWMWA_USE_IMMERSIVE_DARK_MODE
        if (Mica.buildNumber >= Mica.BACKDROP_BUILD_NUM) {
            INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_SYSTEMBACKDROP_TYPE, new IntByReference(DWM_SYSTEMBACKDROP_TYPE.DWMSBT_AUTO.ordinal()), INT_SIZE);
        }
        INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_WINDOW_CORNER_PREFERENCE, new IntByReference(DWM_WINDOW_CORNER_PREFERENCE.DWMWCP_DEFAULT.ordinal()), INT_SIZE);
        INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_BORDER_COLOR, new IntByReference(DWMWA_COLOR_DEFAULT), INT_SIZE);
        INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_CAPTION_COLOR, new IntByReference(DWMWA_COLOR_DEFAULT), INT_SIZE);
        INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_TEXT_COLOR, new IntByReference(DWMWA_COLOR_DEFAULT), INT_SIZE);
    }

    private static int convert(final int color) {
        // Ignore Alpha
        final int b = (color >> 16) & 0xFF;
        final int g = (color >> 8) & 0xFF;
        final int r = color & 0xFF;

        return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    /* ======================================== */

    @NativeType("HRESULT") // Err
    int DwmExtendFrameIntoClientArea(
        HWND hwnd,
        MARGINS pMarInset
    );

    class MARGINS extends Structure {

        public int cxLeftWidth;
        public int cxRightWidth;
        public int cyTopHeight;
        public int cyBottomHeight;

        public MARGINS(final int cxLeftWidth, final int cxRightWidth, final int cyTopHeight, final int cyBottomHeight) {
            this.cxLeftWidth = cxLeftWidth;
            this.cxRightWidth = cxRightWidth;
            this.cyTopHeight = cyTopHeight;
            this.cyBottomHeight = cyBottomHeight;
        }

        @Override
        protected List<String> getFieldOrder() {
            return List.of("cxLeftWidth", "cxRightWidth", "cyTopHeight", "cyBottomHeight");
        }
    }
}
