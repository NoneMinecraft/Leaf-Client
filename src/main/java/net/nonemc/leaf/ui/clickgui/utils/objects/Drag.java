
package net.nonemc.leaf.ui.clickgui.utils.objects;

public class Drag {
    private float xPos, yPos;

    public Drag(float initialXVal, float initialYVal) {
        this.xPos = initialXVal;
        this.yPos = initialYVal;
    }

    public float getX() {
        return xPos;
    }

    public void setX(float x) {
        this.xPos = x;
    }

    public float getY() {
        return yPos;
    }

    public void setY(float y) {
        this.yPos = y;
    }
}
