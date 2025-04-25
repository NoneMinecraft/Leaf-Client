
package net.nonemc.leaf.ui.clickgui.utils.render;

import lombok.Getter;
import lombok.Setter;
import net.nonemc.leaf.ui.clickgui.utils.animations.Animation;
import net.nonemc.leaf.ui.clickgui.utils.animations.Direction;
import net.nonemc.leaf.ui.clickgui.utils.animations.impl.SmoothStepAnimation;
import org.lwjgl.input.Mouse;

/**
 * @author cedo
 * @author Zywl
 */
public class Scroll {

    @Getter
    @Setter
    public float maxScroll = Float.MAX_VALUE, minScroll = 0, rawScroll;
    public float scroll;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);

    public void onScroll(int ms) {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        rawScroll += Mouse.getDWheel() / 4f;
        rawScroll = Math.max(Math.min(minScroll, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }

    public boolean isScrollAnimationDone() {
        return scrollAnimation.isDone();
    }

    public float getScroll() {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        return scroll;
    }

}
