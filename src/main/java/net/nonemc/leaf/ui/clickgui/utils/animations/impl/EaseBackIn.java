
package net.nonemc.leaf.ui.clickgui.utils.animations.impl;

import net.nonemc.leaf.ui.clickgui.utils.animations.Animation;
import net.nonemc.leaf.ui.clickgui.utils.animations.Direction;

public class EaseBackIn extends Animation {
    private final float easeAmount;

    public EaseBackIn(int ms, double endPoint, float easeAmount) {
        super(ms, endPoint);
        this.easeAmount = easeAmount;
    }

    public EaseBackIn(int ms, double endPoint, float easeAmount, Direction direction) {
        super(ms, endPoint, direction);
        this.easeAmount = easeAmount;
    }

    @Override
    protected boolean correctOutput() {
        return true;
    }

    @Override
    protected double getEquation(double x) {
        double x1 = x / duration;
        float shrink = easeAmount + 1;
        return Math.max(0, 1 + shrink * Math.pow(x1 - 1, 3) + easeAmount * Math.pow(x1 - 1, 2));
    }

}
