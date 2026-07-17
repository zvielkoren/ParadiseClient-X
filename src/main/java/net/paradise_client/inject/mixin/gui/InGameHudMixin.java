package net.paradise_client.inject.mixin.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.*;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.scoreboard.*;
import net.paradise_client.*;
import net.paradise_client.event.bus.EventBus;
import net.paradise_client.event.impl.minecraft.HudStartRenderEvent;
import net.paradise_client.mod.HudMod;
import net.paradise_client.protocol.ProtocolVersion;
import net.paradise_client.themes.ThemeManager;
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
@Mixin(InGameHud.class) public abstract class InGameHudMixin {

  /**
   * The Minecraft client instance.
   */
  @Final @Shadow private MinecraftClient client;
  @Shadow @Final private PlayerListHud playerListHud;

  /**
   * Injects behavior at the end of the InGameHud constructor.
   *
   * @param client The Minecraft client instance.
   * @param ci     Callback information for the method.
   */
  @Inject(method = "<init>", at = @At("TAIL")) public void init(MinecraftClient client, CallbackInfo ci) {
  }

  /**
   * Injects behavior at the end of the render method to add custom HUD information.
   *
   * @param context     The DrawContext used for rendering.
   * @param tickCounter The RenderTickCounter for frame timing.
   * @param ci          Callback information for the method.
   */
  @Inject(method = "render", at = @At("TAIL")) public void renderMainHud(DrawContext context,
    RenderTickCounter tickCounter,
    CallbackInfo ci) {
    if (this.client == null) {
      return;
    }

    ArrayList<String> text = new ArrayList<>();

    text.add("§l" + Constants.windowTitle);
    text.add("§7Server §f" +
      ((!Objects.isNull(this.client.getCurrentServerEntry()) && ParadiseClient.HUD_MOD.showServerIP) ?
        this.client.getCurrentServerEntry().address :
        "Hidden"));

    String engine = Objects.isNull(this.client.getNetworkHandler()) ? "§8N/A" :
      this.client.getNetworkHandler().getBrand();
    text.add("§7Engine §f" + (Objects.isNull(engine) ? "§8N/A" : engine.replaceAll("§[0-9a-fk-or]", "")));
    int fps = this.client.getCurrentFps();
    long lastPacket = System.currentTimeMillis() - ParadiseClient.NETWORK_CONFIGURATION.lastPacket;
    String fpsColor = fps >= 60 ? "§a" : fps >= 30 ? "§e" : "§c";
    String packetColor = lastPacket <= 100 ? "§a" : lastPacket <= 1000 ? "§e" : "§c";
    text.add("§7FPS " + fpsColor + fps);
    text.add("§7Last Packet " + packetColor + lastPacket);
    text.add("§7Protocol §f" +
      ProtocolVersion.getProtocolVersion(ParadiseClient.NETWORK_CONFIGURATION.protocolVersion)
        .getVersionIntroducedIn());
    int playerCount = this.client.getNetworkHandler().getPlayerList().size();
    text.add("§7Players §f" + playerCount);

    ParadiseClient.HUD_MOD.hudElements.clear();
    ParadiseClient.HUD_MOD.hudElements.addAll(text);
    EventBus.HUD_START_RENDER_EVENT_CHANNEL.fire(HudStartRenderEvent.INSTANCE);

    int maxWidth = 0;
    for (String s : ParadiseClient.HUD_MOD.hudElements) {
      String cleanText = s.replaceAll("§[0-9a-fk-or]", "");
      maxWidth = Math.max(maxWidth, this.client.textRenderer.getWidth(cleanText));
    }
    int lineHeight = this.client.textRenderer.fontHeight + 2; // Add line spacing
    int panelHeight = ParadiseClient.HUD_MOD.hudElements.size() * lineHeight;
    int padding = 8;
    ThemeManager.renderHudPanel(context, 3, 3, maxWidth + padding * 2, panelHeight + padding * 2);

    int titleBarHeight = lineHeight + 4;
    renderTitleBarAccent(context, 3, 3, maxWidth + padding * 2, titleBarHeight);

    int i = 0;
    for (String s : ParadiseClient.HUD_MOD.hudElements) {
      int textY = 3 + padding + lineHeight * i;

      if (i == 0) {
        String cleanTitle = s.replaceAll("§[0-9a-fk-or]", "");
        int titleWidth = this.client.textRenderer.getWidth(cleanTitle);
        int centeredX = -12 + padding + ((maxWidth + padding) - titleWidth) / 2;
        renderTextWithGlow(context, s, centeredX, textY);
      } else {
        renderText(context, s, 3 + padding, textY);
      }
      i++;
    }

    ParadiseClient.NOTIFICATION_MANAGER.drawNotifications(context, this.client.textRenderer);
  }

  /**
   * Renders a subtle title bar accent at the top of the HUD panel
   */
  @Unique private void renderTitleBarAccent(DrawContext context, int x, int y, int width, int height) {
    int left = 0x604890D5;
    int right = 0x302865B5;

    for (int i = 0; i < width; i++) {
      float ratio = (float) i / width;
      int color = blendColors(left, right, ratio);
      context.fill(x + i, y + 1, x + i + 1, y + height, color);
    }

    int highlightHeight = height / 2;
    for (int i = 0; i < width; i++) {
      float ratio = (float) i / width;
      int alpha = (int) (96 * (1 - ratio));
      int color = (alpha << 24) | 0xFFFFFF;
      context.fill(x + i, y + 1, x + i + 1, y + 1 + highlightHeight, color);
    }
  }

  /**
   * Renders text with a glowing effect
   */
  @Unique private void renderTextWithGlow(DrawContext ct, String s, int x, int y) {
    String cleanText = s.replaceAll("&([0-9a-fk-or])", "§$1");

    // Simple shadow
    ct.drawText(this.client.textRenderer, cleanText, x + 1, y + 1, 0xFF000000, false);

    // Main text
    ct.drawText(this.client.textRenderer, cleanText, x, y, 0xFFFFFFFF, false);
  }

  /**
   * Renders text with an enhanced shadow effect
   */
  @Unique private void renderText(DrawContext ct, String s, int x, int y) {
    String cleanText = s.replaceAll("&([0-9a-fk-or])", "§$1");
    ct.drawText(this.client.textRenderer, cleanText, x, y, -1, false);
  }

  /**
   * Blends two colors together
   */
  @Unique private int blendColors(int color1, int color2, float ratio) {
    ratio = Math.max(0, Math.min(1, ratio));

    int a1 = (color1 >> 24) & 0xFF;
    int r1 = (color1 >> 16) & 0xFF;
    int g1 = (color1 >> 8) & 0xFF;
    int b1 = color1 & 0xFF;

    int a2 = (color2 >> 24) & 0xFF;
    int r2 = (color2 >> 16) & 0xFF;
    int g2 = (color2 >> 8) & 0xFF;
    int b2 = color2 & 0xFF;

    int a = (int) (a1 + (a2 - a1) * ratio);
    int r = (int) (r1 + (r2 - r1) * ratio);
    int g = (int) (g1 + (g2 - g1) * ratio);
    int b = (int) (b1 + (b2 - b1) * ratio);

    return (a << 24) | (r << 16) | (g << 8) | b;
  }

  @Shadow public abstract TextRenderer getTextRenderer();

  @Inject(method = "renderPlayerList", at = @At("HEAD"), cancellable = true)
  private void renderPlayerList(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
    assert this.client.world != null;
    Scoreboard scoreboard = this.client.world.getScoreboard();
    ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);
    if (!this.client.options.playerListKey.isPressed() ||
      this.client.isInSingleplayer() &&
        Objects.requireNonNull(this.client.player).networkHandler.getListedPlayerListEntries().size() <= 1 &&
        scoreboardObjective == null) {
      this.playerListHud.setVisible(false);
      if (ParadiseClient.HUD_MOD.showPlayerList) {
        this.renderTAB(context, context.getScaledWindowWidth(), scoreboard, scoreboardObjective);
      }
    } else {
      this.renderTAB(context, context.getScaledWindowWidth(), scoreboard, scoreboardObjective);
    }
    ci.cancel();
  }

  @Unique
  private void renderTAB(DrawContext context,
    int scaledWindowWidth,
    Scoreboard scoreboard,
    @Nullable ScoreboardObjective scoreboardObjective) {
    this.playerListHud.setVisible(true);
    this.playerListHud.render(context, scaledWindowWidth, scoreboard, scoreboardObjective);
  }
}