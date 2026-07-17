package net.paradise_client.inject.mixin.gui.screen;

import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.network.chat.Component;
import net.minecraft.util.*;
import net.paradise_client.*;
import net.paradise_client.wallpaper.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused") @Mixin(TitleScreen.class) public abstract class TitleScreenMixin extends Screen {
  @Nullable @Shadow private SplashRenderer splash;
  @Nullable @Shadow private RealmsNotificationsScreen realmsNotificationsScreen;
  @Shadow private boolean fading;
  @Shadow private long fadeInStart;
  @Final @Shadow private LogoRenderer logoRenderer;

  protected TitleScreenMixin(Component title) {
    super(title);
  }

  @Inject(method = "init", at = @At(value = "TAIL")) public void init(CallbackInfo ci) {
    Component updateMessage1 = Helper.parseColoredText("&2Current version: &1" +
      Constants.VERSION +
      " &2Latetst version: &1" +
      ParadiseClient.MISC_MOD.latestVersion +
      " &fClick to download");
    if (ParadiseClient.MISC_MOD.isClientOutdated) {
      this.addRenderableWidget(new PlainTextButton(this.width - this.font.width(updateMessage1) - 2,
        this.height - 20,
        this.font.width(updateMessage1),
        10,
        updateMessage1,
        (button) -> {
          Util.getPlatform().openUri("https://paradise-client.net/downloads");
          Minecraft.getInstance().gui.setScreen(new TitleScreen());
        },
        this.font));
    }

    Theme currentTheme = ThemeRenderer.getTheme();

    this.addRenderableWidget(Button.builder(Component.literal("Theme: " + currentTheme.getName()), onPress -> {
      Theme[] themes = Theme.values();
      int nextOrdinal = (ThemeRenderer.getTheme().ordinal() + 1) % themes.length;
      Theme nextTheme = themes[nextOrdinal];
      ThemeRenderer.setTheme(nextTheme);
      onPress.setMessage(Component.literal("Theme: " + nextTheme.getName()));
    }).width(150).pos(this.width / 2 - 75, this.height / 4 + 160).build());
  }

  @Inject(method = "extractRenderState",
    at = @At(value = "INVOKE",
      target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V"))
  public void renderCustomText(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    float widgetFade = 1.0F;
    if (this.fading) {
      float fade = (float) (Util.getMillis() - this.fadeInStart) / 2000.0F;
      fade = Mth.clamp(fade, 0.0F, 1.0F);
      widgetFade = Mth.clampedMap(fade, 0.5F, 1.0F, 0.0F, 1.0F);
    }
    graphics.text(this.font, Constants.windowTitle, 2, this.height - 22, ARGB.white(widgetFade));
  }
}
