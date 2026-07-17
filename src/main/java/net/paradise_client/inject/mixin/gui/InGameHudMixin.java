package net.paradise_client.inject.mixin.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.paradise_client.*;
import net.paradise_client.event.bus.EventBus;
import net.paradise_client.event.impl.minecraft.HudStartRenderEvent;
import net.paradise_client.protocol.ProtocolVersion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static net.paradise_client.Helper.*;

/**
 * Mixin for the InGameHud class to inject custom HUD rendering behavior. This mixin is used to display additional
 * information on the HUD.
 *
 * @author SpigotRCE
 * @since 1.0
 */
@Mixin(Gui.class) public abstract class InGameHudMixin {

  /**
   * The Minecraft client instance.
   */
  @Final @Shadow private Minecraft minecraft;
  @Shadow @Final private PlayerTabOverlay tabList;

  /**
   * Injects behavior at the end of the InGameHud constructor.
   *
   * @param client The Minecraft client instance.
   * @param ci     Callback information for the method.
   */
  @Inject(method = "<init>", at = @At("TAIL")) public void init(Minecraft client, CallbackInfo ci) {
  }

  /**
   * Injects behavior at the end of the render method to add custom HUD information.
   *
   * @param context     The DrawContext used for rendering.
   * @param tickCounter The RenderTickCounter for frame timing.
   * @param ci          Callback information for the method.
   */
  @Inject(method = "render", at = @At("TAIL")) public void renderMainHud(GuiGraphics context,
    DeltaTracker tickCounter,
    CallbackInfo ci) {
    if (this.minecraft == null) {
      return;
    }

    ArrayList<String> text = new ArrayList<>();

    text.add(Constants.windowTitle);
    text.add("Server " +
      ((!Objects.isNull(this.minecraft.getCurrentServer()) && ParadiseClient.HUD_MOD.showServerIP) ?
        this.minecraft.getCurrentServer().ip :
        "Hidden"));
    text.add("Engine " +
      (Objects.isNull(this.minecraft.getConnection()) ? "" : this.minecraft.getConnection().serverBrand()));
    text.add("FPS " + this.minecraft.getFps());
    text.add("Protocol " +
      ProtocolVersion.getProtocolVersion(ParadiseClient.NETWORK_CONFIGURATION.protocolVersion)
        .getVersionIntroducedIn());
    text.add("Players " + this.minecraft.getConnection().getOnlinePlayers().size());

    ParadiseClient.HUD_MOD.hudElements.clear();
    ParadiseClient.HUD_MOD.hudElements.addAll(text);
    EventBus.HUD_START_RENDER_EVENT_CHANNEL.fire(HudStartRenderEvent.INSTANCE);

    int i = 0;
    for (String s : ParadiseClient.HUD_MOD.hudElements) {
      renderTextWithChroma(context, s, 5, 5 + this.minecraft.font.lineHeight * i);
      i++;
    }

    ParadiseClient.NOTIFICATION_MANAGER.drawNotifications(context, this.minecraft.font);
  }

  /**
   * Renders text with a chroma color effect.
   *
   * @param ct The DrawContext used for rendering.
   * @param s  The string to render.
   * @param x  The x-coordinate for the text.
   * @param y  The y-coordinate for the text.
   */
  @SuppressWarnings("SameParameterValue") @Unique private void renderTextWithChroma(GuiGraphics ct,
    String s,
    int x,
    int y) {
    char[] chars = s.toCharArray();
    int i = 0;
    for (char aChar : chars) {
      String c = String.valueOf(aChar);
      ct.drawString(this.minecraft.font,
        c,
        x + i,
        y,
        getChroma(((int) Math.sqrt(x * x + y * y) * 10) + (i * -17), 1, 1).getRGB(),
        false);
      i += getFont().width(c);
    }
  }

  /**
   * Gets the TextRenderer instance used for rendering text.
   *
   * @return The TextRenderer instance.
   */
  @Shadow public abstract Font getFont();

  @Inject(method = "renderTabList", at = @At("HEAD"), cancellable = true)
  private void renderPlayerList(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
    assert this.minecraft.level != null;
    Scoreboard scoreboard = this.minecraft.level.getScoreboard();
    Objective scoreboardObjective = scoreboard.getDisplayObjective(DisplaySlot.LIST);
    if (!this.minecraft.options.keyPlayerList.isDown() ||
      this.minecraft.isLocalServer() &&
        Objects.requireNonNull(this.minecraft.player).connection.getListedOnlinePlayers().size() <= 1 &&
        scoreboardObjective == null) {
      this.tabList.setVisible(false);
      if (ParadiseClient.HUD_MOD.showPlayerList) {
        this.renderTAB(context, context.guiWidth(), scoreboard, scoreboardObjective);
      }
    } else {
      this.renderTAB(context, context.guiWidth(), scoreboard, scoreboardObjective);
    }
    ci.cancel();
  }

  @Unique
  private void renderTAB(GuiGraphics context,
    int scaledWindowWidth,
    Scoreboard scoreboard,
    @Nullable Objective scoreboardObjective) {
    this.tabList.setVisible(true);
    this.tabList.render(context, scaledWindowWidth, scoreboard, scoreboardObjective);
  }
}
