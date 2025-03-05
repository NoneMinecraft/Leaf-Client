package net.nonemc.leaf.utils;

import net.nonemc.leaf.features.module.Module;
import net.nonemc.leaf.value.BoolValue;

public class AnimationHelper {
    public float animationX;
    public int alpha;

    public AnimationHelper() {
        this.alpha = 0;
    }

    public AnimationHelper(BoolValue value) {
        animationX = value.get() ? 5 : -5;
    }

    public AnimationHelper(Module module) {
        animationX = module.getState() ? 5 : -5;
    }

    public int getAlpha() {
        return this.alpha;
    }

    public float getAnimationX() {
        return this.animationX;
    }

    public void resetAlpha() {
        this.alpha = 0;
    }

    public void updateAlpha(int speed) {
        if (alpha < 255)
            this.alpha += speed;
    }
}
