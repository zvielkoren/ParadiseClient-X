package net.paradise_client.inject.mixin.gui;

import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.*;
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

@Mixin(Hud.class) public class HudMixin {
  /**
   * The Minecraft client instance.
   */
  @Shadow @Final private Minecraft minecraft;

  @Shadow @Final private PlayerTabOverlay tabList;

  @Inject(method = "extractTabList", at = @At("HEAD"), cancellable = true)
  private void renderPlayerList(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
    assert this.minecraft.level != null;
    Scoreboard scoreboard = this.minecraft.level.getScoreboard();
    Objective scoreboardObjective = scoreboard.getDisplayObjective(DisplaySlot.LIST);
    if (!this.minecraft.options.keyPlayerList.isDown() ||
      this.minecraft.isLocalServer() &&
        Objects.requireNonNull(this.minecraft.player).connection.getListedOnlinePlayers().size() <= 1 &&
        scoreboardObjective == null) {
      this.tabList.setVisible(false);
      if (ParadiseClient.HUD_MOD.showPlayerList) {
        this.renderTAB(graphics, graphics.guiWidth(), scoreboard, scoreboardObjective);
      }
    } else {
      this.renderTAB(graphics, graphics.guiWidth(), scoreboard, scoreboardObjective);
    }
    ci.cancel();
  }

  @Unique
  private void renderTAB(GuiGraphicsExtractor context,
    int scaledWindowWidth,
    Scoreboard scoreboard,
    @Nullable Objective scoreboardObjective) {
    this.tabList.setVisible(true);
    this.tabList.extractRenderState(context, scaledWindowWidth, scoreboard, scoreboardObjective);
  }

  /**
   * Injects behavior at the end of the render method to add custom HUD information.
   *
   * @param ci Callback information for the method.
   */
  @Inject(method = "extractRenderState", at = @At("TAIL")) public void renderMainHud(GuiGraphicsExtractor graphics,
    DeltaTracker deltaTracker,
    CallbackInfo ci) {
    if (this.minecraft == null) {
      return;
    }

    ArrayList<String> text = new ArrayList<>();
    var connection = this.minecraft.getConnection(); // Fetch active client connection profile securely

    text.add(Constants.windowTitle);
    text.add("Server " +
      ((!Objects.isNull(this.minecraft.getCurrentServer()) && ParadiseClient.HUD_MOD.showServerIP) ?
        this.minecraft.getCurrentServer().ip :
        "Hidden"));
    text.add("Engine " + (Objects.isNull(connection) ? "Offline" : connection.serverBrand()));
    text.add("FPS " + this.minecraft.getFps());
    text.add("Protocol " +
      ProtocolVersion.getProtocolVersion(ParadiseClient.NETWORK_CONFIGURATION.protocolVersion)
        .getVersionIntroducedIn());

    text.add("Players " + (Objects.isNull(connection) ? "-1" : connection.getOnlinePlayers().size()));

    ParadiseClient.HUD_MOD.hudElements.clear();
    ParadiseClient.HUD_MOD.hudElements.addAll(text);
    EventBus.HUD_START_RENDER_EVENT_CHANNEL.fire(HudStartRenderEvent.INSTANCE);

    int i = 0;
    int xMouse = (int) this.minecraft.mouseHandler.getScaledXPos(this.minecraft.getWindow());
    int yMouse = (int) this.minecraft.mouseHandler.getScaledYPos(this.minecraft.getWindow());
    for (String s : ParadiseClient.HUD_MOD.hudElements) {
      renderTextWithChroma(graphics, s, 5, 5 + this.minecraft.font.lineHeight * i);
      i++;
    }

    ParadiseClient.NOTIFICATION_MANAGER.drawNotifications(graphics, this.minecraft.font);
  }

  /**
   * Renders text with a chroma color effect.
   *
   * @param ct The DrawContext used for rendering.
   * @param s  The string to render.
   * @param x  The x-coordinate for the text.
   * @param y  The y-coordinate for the text.
   */
  @SuppressWarnings("SameParameterValue") @Unique private void renderTextWithChroma(GuiGraphicsExtractor ct,
    String s,
    int x,
    int y) {
    char[] chars = s.toCharArray();
    int i = 0;
    for (char aChar : chars) {
      String c = String.valueOf(aChar);
      ct.text(this.minecraft.font,
        c,
        x + i,
        y,
        getChroma(((int) Math.sqrt(x * x + y * y) * 10) + (i * -17), 1, 1).getRGB(),
        false);
      i += minecraft.font.width(c);
    }
  }
}
