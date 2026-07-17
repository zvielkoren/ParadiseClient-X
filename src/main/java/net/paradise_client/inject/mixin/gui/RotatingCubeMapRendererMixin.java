package net.paradise_client.inject.mixin.gui;

import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.paradise_client.wallpaper.ThemeRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PanoramaRenderer.class) public class RotatingCubeMapRendererMixin {
  @Inject(method = "render", at = @At("HEAD"), cancellable = true)
  public void render(GuiGraphics context, int width, int height, float alpha, float tickDelta, CallbackInfo ci) {
    // Calls dynamic rendering based on the defined theme
    ThemeRenderer.render(context, width, height);
    ci.cancel();
  }
}
