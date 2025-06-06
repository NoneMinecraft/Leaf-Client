package net.nonemc.leaf.ui.clickgui.elements;

import net.nonemc.leaf.libs.base.MinecraftInstance;

public class Element extends MinecraftInstance {

    private int x;
    private int y;
    private int width;
    private int height;
    private boolean visible;

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void drawScreen(int mouseX, int mouseY, float button) {
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
