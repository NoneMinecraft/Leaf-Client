package net.nonemc.leaf.utils.render;

public final class AnimationUtils {
    public static float animate(float target, float current, float speed) {
        if (current == target) return current;

        boolean larger = target > current;
        if (speed < 0.0F) {
            speed = 0.0F;
        } else if (speed > 1.0F) {
            speed = 1.0F;
        }

        double dif = Math.max(target, (double) current) - Math.min(target, (double) current);
        double factor = dif * (double) speed;
        if (factor < 0.1D) {
            factor = 0.1D;
        }

        if (larger) {
            current += (float) factor;
            if (current >= target) current = target;
        } else if (target < current) {
            current -= (float) factor;
            if (current <= target) current = target;
        }

        return current;
    }
}