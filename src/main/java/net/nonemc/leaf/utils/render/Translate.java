package net.nonemc.leaf.utils.render;

import net.nonemc.leaf.launch.data.modernui.AnimationUtil;
import net.nonemc.leaf.utils.AnimationUtils;

public final class Translate {
    private float x;
    private float y;
    private boolean first = false;

    public Translate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void interpolate(float targetX, float targetY, double smoothing) {
        if (first) {
            this.x = AnimationUtil.animate(targetX, this.x, smoothing);
            this.y = AnimationUtil.animate(targetY, this.y, smoothing);
        } else {
            this.x = targetX;
            this.y = targetY;
            first = true;
        }
    }

    public void translate(float targetX, float targetY) {
        x = AnimationUtils.lstransition(x, targetX, 0.0);
        y = AnimationUtils.lstransition(y, targetY, 0.0);
    }

    public void translate(float targetX, float targetY, double speed) {
        x = AnimationUtils.lstransition(x, targetX, speed);
        y = AnimationUtils.lstransition(y, targetY, speed);
    }

    public void interpolate2(float targetX, float targetY, double smoothing) {
        this.x = targetX;
        this.y = AnimationUtil.animate(targetY, this.y, smoothing);
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }
}

