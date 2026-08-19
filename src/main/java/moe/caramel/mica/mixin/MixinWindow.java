package moe.caramel.mica.mixin;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.MonitorManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.systems.GpuBackend;
import moe.caramel.mica.natives.DwmApi;
import moe.caramel.mica.natives.NtDll;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public final class MixinWindow {

    @Shadow @Final private long handle;
    @Shadow private boolean fullscreen;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void init(
        final WindowEventHandler handler, final DisplayData displayData,
        final String fullscreenVideoModeString, final boolean exclusiveFullscreen,
        final String title, final MonitorManager monitorManager, final GpuBackend backend,
        final CallbackInfo ci
    ) {
        // Check OS
        if (Util.getPlatform() != Util.OS.WINDOWS) {
            return;
        }

        // Initialize Mica
        NtDll.getBuildNumber();
        DwmApi.updateDwm(this.fullscreen, this.handle);
    }

    @Inject(method = "updateFullscreenIfChanged", at = @At(value = "TAIL"))
    private void updateFullscreenIfChanged(final CallbackInfo ci) {
        // Check OS
        if (Util.getPlatform() != Util.OS.WINDOWS) {
            return;
        }

        // Update DWM
        DwmApi.updateDwm(this.fullscreen, this.handle);
    }
}
