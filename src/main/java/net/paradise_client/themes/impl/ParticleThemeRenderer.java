package net.paradise_client.themes.impl;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.paradise_client.themes.AbstractThemeRenderer;

import java.util.Random;

public class ParticleThemeRenderer extends AbstractThemeRenderer {
  private static final Random random = new Random();
  private static final Particle[] particles = new Particle[100];
  private static int lastWidth = -1;
  private static int lastHeight = -1;

  static {
    for (int i = 0; i < particles.length; i++) {
      particles[i] = new Particle();
    }
  }

  @Override public String getThemeName() {
    return "Particle";
  }

  @Override public void renderBackground(DrawContext context, int width, int height) {
    if (width != lastWidth || height != lastHeight) {
      for (Particle p : particles) {
        p.reset(width, height);
      }
      lastWidth = width;
      lastHeight = height;
    }

    context.fillGradient(0, 0, width, height, 0x801A237E, 0x80882dbd);

    for (Particle p : particles) {
      p.update(width, height);
      context.fill(p.x, p.y, p.x + 2, p.y + 2, 0x80FFFFFF);
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
    int topColor = pressed ? 0xCC4A3A7E : hovered ? 0xCC6A5A9E : 0xCC3A2A6E;
    int bottomColor = pressed ? 0xCC2A1A4E : hovered ? 0xCC4A3A7E : 0xCC2A1A5E;

    drawVerticalGradient(context, x, y, width, height, topColor, bottomColor);

    int borderColor = hovered ? 0xFFAAAAFF : 0xFF6A5A8E;
    context.drawBorder(x, y, width, height, borderColor);

    if (text != null && font != null) {
      int textX = x + (width - font.getWidth(text)) / 2;
      int textY = y + (height - 8) / 2;
      context.drawText(font, text, textX, textY, 0xFFFFFFFF, true);
    }
  }

  @Override public void renderHudPanel(DrawContext context, int x, int y, int width, int height) {
    context.fill(x, y, x + width, y + height, 0xCC2A1A5E);
    context.drawBorder(x, y, width, height, 0xFF6A5A8E);
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
    drawHorizontalGradient(context, x, y, width, height, 0xDD4A3A7E, 0xDD2A1A5E);
    if (title != null && font != null) {
      context.drawText(font, title, x + 8, y + (height - 8) / 2, 0xFFFFFFFF, true);
    }
  }

  @Override public void renderTaskbar(DrawContext context, int screenWidth, int screenHeight, int height) {
    int y = screenHeight - height;
    drawVerticalGradient(context, 0, y, screenWidth, height, 0xDD3A2A6E, 0xDD1A0A4E);
  }

  @Override public void renderPanel(DrawContext context, int x, int y, int width, int height) {
    context.fill(x, y, x + width, y + height, 0xCC2A1A5E);
  }

  @Override public void renderTextField(DrawContext context, int x, int y, int width, int height, boolean focused) {
    context.fill(x, y, x + width, y + height, 0xFF1A0A3E);
    context.drawBorder(x, y, width, height, focused ? 0xFFAAAAFF : 0xFF6A5A8E);
  }

  @Override public void renderSelection(DrawContext context, int x, int y, int width, int height) {
    context.fill(x, y, x + width, y + height, 0x884A3A7E);
  }

  @Override public void renderProgressBar(DrawContext context, int x, int y, int width, int height, float progress) {
    context.fill(x, y, x + width, y + height, 0xFF1A0A3E);
    int fillWidth = (int) (width * progress);
    drawVerticalGradient(context, x, y, fillWidth, height, 0xFF6A5A9E, 0xFF4A3A7E);
    context.drawBorder(x, y, width, height, 0xFF6A5A8E);
  }

  @Override
  public void renderSlider(DrawContext context, int x, int y, int width, int height, float value, boolean hovered) {
    context.fill(x, y + height / 2 - 1, x + width, y + height / 2 + 1, 0xFF3A2A6E);
    int thumbX = x + (int) ((width - 8) * value);
    renderButton(context, thumbX, y, 8, height, hovered, false, null, null);
  }

  @Override public void renderCheckbox(DrawContext context, int x, int y, int size, boolean checked, boolean hovered) {
    context.fill(x, y, x + size, y + size, 0xFF1A0A3E);
    context.drawBorder(x, y, size, size, 0xFF6A5A8E);
    if (checked) {
      context.fill(x + 2, y + 2, x + size - 2, y + size - 2, 0xFFAAAAFF);
    }
  }

  @Override public void renderSeparator(DrawContext context, int x, int y, int width) {
    context.fill(x, y, x + width, y + 1, 0xFF6A5A8E);
  }

  private static class Particle {
    int x, y, speedX, speedY;

    public void reset(int width, int height) {
      x = random.nextInt(Math.max(width, 1));
      y = random.nextInt(Math.max(height, 1));
      do {
        speedX = -1 + random.nextInt(3);
        speedY = -1 + random.nextInt(3);
      } while (speedX == 0 && speedY == 0);
    }

    public void update(int width, int height) {
      x += speedX;
      y += speedY;
      if (x < 0 || x > width || y < 0 || y > height) {
        reset(width, height);
      }
    }
  }
}