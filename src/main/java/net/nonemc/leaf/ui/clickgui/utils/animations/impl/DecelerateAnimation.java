
package net.nonemc.leaf.ui.clickgui.utils.animations.impl;

import net.nonemc.leaf.ui.clickgui.utils.animations.Animation;
import net.nonemc.leaf.ui.clickgui.utils.animations.Direction;

public class DecelerateAnimation extends Animation {

    public DecelerateAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public DecelerateAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    protected double getEquation(double x) {
        double x1 = x / duration;
        return 1 - ((x1 - 1) * (x1 - 1));
    }
}
