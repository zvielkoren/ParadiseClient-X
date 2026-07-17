package net.paradise_client.themes.impl;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.paradise_client.themes.AbstractThemeRenderer;

/**
 * Modern theme with sleek gradients, shadows, and a futuristic aesthetic with animations
 */
public class ModernThemeRenderer extends AbstractThemeRenderer {

  private static final int BACKGROUND_TOP = 0xFF1A1A2E;
  private static final int BACKGROUND_BOTTOM = 0xFF16213E;
  private static final int PANEL_COLOR = 0xCC2A2E48;
  private static final int BUTTON_BASE = 0xFF3A3F5C;
  private static final int BUTTON_HOVER = 0xFF4A5F78;
  private static final int BUTTON_PRESSED = 0xFF2E3A4C;
  private static final int TEXT_COLOR = 0xFFFFFFFF;
  private static final int ACCENT_COLOR = 0xFF00D4FF;

  private int animationFrame = 0;

  @Override public void renderBackground(DrawContext context, int width, int height) {
    drawVerticalGradient(context, 0, 0, width, height, BACKGROUND_TOP, BACKGROUND_BOTTOM);
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
    int baseColor = pressed ? BUTTON_PRESSED : hovered ? BUTTON_HOVER : BUTTON_BASE;
    context.fill(x, y, x + width, y + height, baseColor);
    drawHorizontalGradient(context, x, y, width, height, baseColor, blendColors(baseColor, ACCENT_COLOR, 0.3f));
    int textX = x + (width - font.getWidth(text)) / 2;
    int textY = y + (height - font.fontHeight) / 2;
    context.drawText(font, text, textX, textY, TEXT_COLOR, false);
  }

  @Override public void renderHudPanel(DrawContext context, int x, int y, int width, int height) {
    float alpha = (float) Math.sin(animationFrame * 0.1) * 0.1f + 0.7f; // Fade effect between 0.6 and 0.8
    int animatedColor = (int) (alpha * 255) << 24 | (PANEL_COLOR & 0xFFFFFF);
    context.fill(x, y, x + width, y + height, animatedColor);
    drawVerticalGradient(context, x + 1, y + 1, width - 2, height - 2, 0x22FFFFFF, 0x11000000);
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
    float alpha = (float) Math.cos(animationFrame * 0.05) * 0.1f + 0.9f; // Subtle pulse between 0.8 and 1.0
    int color = active ? blendColors(BUTTON_BASE, ACCENT_COLOR, 0.5f) : BUTTON_BASE;
    int animatedColor = (int) (alpha * 255) << 24 | (color & 0xFFFFFF);
    context.fill(x, y, x + width, y + height, animatedColor);
    int textX = x + (width - font.getWidth(title)) / 2;
    int textY = y + (height - font.fontHeight) / 2;
    context.drawText(font, title, textX, textY, TEXT_COLOR, false);
  }

  @Override public void renderTaskbar(DrawContext context, int screenWidth, int screenHeight, int height) {
    int y = screenHeight - height;
    context.fill(0, y, screenWidth, y + height, BUTTON_BASE);
    float pulse = (float) Math.sin(animationFrame * 0.08) * 0.1f + 0.2f; // Pulse effect between 0.1 and 0.3
    int accentColor = blendColors(BUTTON_BASE, ACCENT_COLOR, pulse);
    drawHorizontalGradient(context, 0, y, screenWidth, height, BUTTON_BASE, accentColor);
  }

  @Override public void renderPanel(DrawContext context, int x, int y, int width, int height) {
    context.fill(x, y, x + width, y + height, PANEL_COLOR);
    drawVerticalGradient(context, x + 1, y + 1, width - 2, height - 2, 0x33000000, 0x11000000);
  }

  @Override public void renderTextField(DrawContext context, int x, int y, int width, int height, boolean focused) {
    int borderColor = focused ? ACCENT_COLOR : 0xFF555555;
    context.fill(x, y, x + width, y + height, 0xFF2A2E48);
    context.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF3A3F5C);
    context.drawHorizontalLine(x, x + width, y, borderColor);
    context.drawHorizontalLine(x, x + width, y + height - 1, borderColor);
    context.drawVerticalLine(x, y, y + height, borderColor);
    context.drawVerticalLine(x + width - 1, y, y + height, borderColor);
  }

  @Override public void renderSelection(DrawContext context, int x, int y, int width, int height) {
    context.fill(x, y, x + width, y + height, blendColors(0x00000000, ACCENT_COLOR, 0.2f));
  }

  @Override public void renderProgressBar(DrawContext context, int x, int y, int width, int height, float progress) {
    int fillWidth = (int) (width * Math.max(0, Math.min(1, progress)));
    context.fill(x, y, x + width, y + height, 0xFF2A2E48);
    context.fill(x, y, x + fillWidth, y + height, ACCENT_COLOR);
  }

  @Override
  public void renderSlider(DrawContext context, int x, int y, int width, int height, float value, boolean hovered) {
    int fillWidth = (int) (width * Math.max(0, Math.min(1, value)));
    context.fill(x, y, x + width, y + height, 0xFF2A2E48);
    context.fill(x, y, x + fillWidth, y + height, hovered ? blendColors(ACCENT_COLOR, 0xFFFFFF, 0.2f) : ACCENT_COLOR);
    int knobX = x + fillWidth - height / 2;
    context.fill(knobX, y, knobX + height, y + height, 0xFF3A3F5C);
  }

  @Override public void renderCheckbox(DrawContext context, int x, int y, int size, boolean checked, boolean hovered) {
    context.fill(x, y, x + size, y + size, BUTTON_BASE);
    if (checked) {
      context.fill(x + 2, y + 2, x + size - 2, y + size - 2, ACCENT_COLOR);
    }
    if (hovered) {
      context.fill(x, y, x + size, y + size, blendColors(0x00000000, 0xFFFFFF, 0.1f));
    }
  }

  @Override public void renderSeparator(DrawContext context, int x, int y, int width) {
    context.fill(x, y, x + width, y + 1, 0xFF555555);
  }

  @Override public String getThemeName() {
    return "Modern";
  }

  @Override public void update() {
    animationFrame = (animationFrame + 1) % 360;
  }
}