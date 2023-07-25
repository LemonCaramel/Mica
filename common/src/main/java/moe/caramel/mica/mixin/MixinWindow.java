package moe.caramel.mica.mixin;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import moe.caramel.mica.natives.DwmApi;
import moe.caramel.mica.natives.NtDll;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public final class MixinWindow {

    @Shadow @Final private long window;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void init(
        final WindowEventHandler handler, final ScreenManager manager,
        final DisplayData display, final String videoMode, final String title,
        final CallbackInfo ci
    ) {
        NtDll.getBuildNumber();
        DwmApi.updateDwm(this.window);
    }
}
