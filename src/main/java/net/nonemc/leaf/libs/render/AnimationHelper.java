package net.nonemc.leaf.libs.render;

import lombok.Getter;
import net.nonemc.leaf.features.module.Module;
import net.nonemc.leaf.value.BoolValue;

public class AnimationHelper {
    public float animationX;
    @Getter
    public int alpha;

    public AnimationHelper(BoolValue value) {
        animationX = value.get() ? 5 : -5;
    }

    public AnimationHelper(Module module) {
        animationX = module.getState() ? 5 : -5;
    }
}
