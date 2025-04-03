package net.nonemc.leaf.launch.data.modernui.mainmenu;

import net.minecraft.client.renderer.GlStateManager;
import net.nonemc.leaf.launch.data.modernui.mainmenu.config.configs.ButtonConfig;
import net.nonemc.leaf.ui.font.Fonts;
import net.nonemc.leaf.utils.render.RenderUtils;

import java.awt.*;

public class MainMenuButton {
    private final String text;
    private final Runnable action;
    private final float baseWidth;
    private final float baseHeight;
    private float x;
    private float y;
    private float xScale;
    private float yScale;
    private float textScale;
    private int color;
    private int cornerRadius;

    public MainMenuButton(String text, Runnable action,
                          float baseWidth, float baseHeight,
                          float x, float y,
                          float xScale, float yScale, float textScale,
                          int color, int cornerRadius) {
        this.text = text;
        this.action = action;
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
        this.x = x;
        this.y = y;
        this.xScale = Math.max(0.5f, Math.min(2.0f, xScale));
        this.yScale = Math.max(0.5f, Math.min(2.0f, yScale));
        this.textScale = Math.max(0.5f, Math.min(2.0f, textScale));
        this.color = color;
        this.cornerRadius = Math.max(0, cornerRadius);
    }

    public void draw(int mouseX, int mouseY) {
        float scaledWidth = getScaledWidth();
        float scaledHeight = getScaledHeight();
        float drawX = x - scaledWidth / 2;
        float drawY = y - scaledHeight / 2;
        RenderUtils.drawRoundedRect(drawX, drawY,
                drawX + scaledWidth, drawY + scaledHeight,
                cornerRadius, color);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(textScale, textScale, 1.0f);
        Fonts.font40.drawCenteredString(
                text,
                0,
                -5.25f,
                new Color(255, 255, 255, getTextAlpha()).getRGB()
        );
        GlStateManager.popMatrix();
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        float halfWidth = getScaledWidth() / 2;
        float halfHeight = getScaledHeight() / 2;
        return mouseX >= x - halfWidth &&
                mouseX <= x + halfWidth &&
                mouseY >= y - halfHeight &&
                mouseY <= y + halfHeight;
    }

    public void mouseClick(int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            action.run();
        }
    }
    public void scaleXSize(float scaleFactor) {
        xScale = Math.max(0.5f, Math.min(2.0f, xScale * scaleFactor));
    }

    public void scaleYSize(float scaleFactor) {
        yScale = Math.max(0.5f, Math.min(2.0f, yScale * scaleFactor));
    }

    public void scaleBothSize(float scaleFactor) {
        scaleXSize(scaleFactor);
        scaleYSize(scaleFactor);
    }

    public void applyConfig(ButtonConfig config) {
        this.x = config.x;
        this.y = config.y;
        this.xScale = Math.max(0.5f, Math.min(2.0f, config.xScale));
        this.yScale = Math.max(0.5f, Math.min(2.0f, config.yScale));
        this.textScale = Math.max(0.5f, Math.min(2.0f, config.textScale));
        this.color = config.color;
        this.cornerRadius = Math.max(0, config.cornerRadius);
    }

    public ButtonConfig getConfig() {
        return new ButtonConfig(x, y, xScale, yScale, textScale, color, cornerRadius);
    }
    public float getXScale() { return xScale; }
    public float getYScale() { return yScale; }
    public float getTextScale() { return textScale; }

    public String getText() {
        return text;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    private float getScaledWidth() { return baseWidth * xScale; }
    private float getScaledHeight() { return baseHeight * yScale; }

    public int getColor() {
        return color;
    }
    private int getTextAlpha() { return (color >> 24) & 0xFF; }
    public int getCornerRadius() {
        return cornerRadius;
    }
}
