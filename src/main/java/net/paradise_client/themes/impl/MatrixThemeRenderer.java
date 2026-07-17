package net.paradise_client.themes.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.paradise_client.Helper;
import net.paradise_client.themes.AbstractThemeRenderer;

import java.util.Random;

/**
 * Matrix-style falling characters theme
 */
public class MatrixThemeRenderer extends AbstractThemeRenderer {
  private static final Random random = new Random();
  private static final int[] drops = new int[300];

  @Override public String getThemeName() {
    return "Matrix";
  }

  @Override public void renderBackground(DrawContext context, int width, int height) {
    context.fillGradient(0, 0, width, height, 0xCC000000, 0xCC000000);

    for (int i = 0; i < drops.length; i++) {
      String text = Helper.generateRandomString(1, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", random);
      context.drawText(MinecraftClient.getInstance().textRenderer, text, i * 10, drops[i] * 10, 0x00FF00, false);

      if (drops[i] * 10 > height && random.nextDouble() > 0.975) {
        drops[i] = 0;
      }
      drops[i]++;
    }
  }

  @Override
  public void renderButton(DrawContext context,
    int x,
    int y,
    int width,
    int height,
    boolean hovered,
    boolean pressed,
    String text,
    TextRenderer font) {
    int bgColor = pressed ? 0xCC001100 : hovered ? 0xCC003300 : 0xCC000000;
    context.fill(x, y, x + width, y + height, bgColor);

    int borderColor = hovered ? 0xFF00FF00 : 0xFF008800;
    context.drawBorder(x, y, width, height, borderColor);

    if (text != null && font != null) {
      int textX = x + (width - font.getWidth(text)) / 2;
      int textY = y + (height - 8) / 2;
      context.drawText(font, text, textX, textY, 0xFF00FF00, false);
    }
  }

  @Override public void renderHudPanel(DrawContext context, int x, int y, int width, int height) {
    context.fill(x, y, x + width, y + height, 0xCC000000);
    context.drawBorder(x, y, width, height, 0xFF008800);
  }

  @Override
  public void renderTitleBar(DrawContext context,
    int x,
    int y,
    int width,
    int height,
    String title,
    TextRenderer font,
    boolean active) {
    context.fill(x, y, x + width, y + height, 0xDD001100);
    if (title != null && font != null) {
      context.drawText(font, title, x + 8, y + (height - 8) / 2, 0xFF00FF00, true);
    }
  }

  @Override public void renderTaskbar(DrawContext context, int screenWidth, int screenHeight, int height) {
    int y = screenHeight - height;
    context.fill(0, y, screenWidth, y + height, 0xDD000000);
    context.fill(0, y, screenWidth, y + 1, 0xFF00FF00);
  }

  @Override public void renderPanel(DrawContext context, int x, int y, int width, int height) {
    context.fill(x, y, x + width, y + height, 0xCC000000);
  }

  @Override public void renderTextField(DrawContext context, int x, int y, int width, int height, boolean focused) {
    context.fill(x, y, x + width, y + height, 0xFF001100);
    context.drawBorder(x, y, width, height, focused ? 0xFF00FF00 : 0xFF008800);
  }

  @Override public void renderSelection(DrawContext context, int x, int y, int width, int height) {
    context.fill(x, y, x + width, y + height, 0x8800FF00);
  }

  @Override public void renderProgressBar(DrawContext context, int x, int y, int width, int height, float progress) {
    context.fill(x, y, x + width, y + height, 0xFF001100);
    int fillWidth = (int) (width * progress);
    context.fill(x, y, x + fillWidth, y + height, 0xFF00FF00);
    context.drawBorder(x, y, width, height, 0xFF008800);
  }

  @Override
  public void renderSlider(DrawContext context, int x, int y, int width, int height, float value, boolean hovered) {
    context.fill(x, y + height / 2 - 1, x + width, y + height / 2 + 1, 0xFF003300);
    int thumbX = x + (int) ((width - 8) * value);
    renderButton(context, thumbX, y, 8, height, hovered, false, null, null);
  }

  @Override public void renderCheckbox(DrawContext context, int x, int y, int size, boolean checked, boolean hovered) {
    context.fill(x, y, x + size, y + size, 0xFF001100);
    context.drawBorder(x, y, size, size, 0xFF008800);
    if (checked) {
      context.fill(x + 2, y + 2, x + size - 2, y + size - 2, 0xFF00FF00);
    }
  }

  @Override public void renderSeparator(DrawContext context, int x, int y, int width) {
    context.fill(x, y, x + width, y + 1, 0xFF008800);
  }
}