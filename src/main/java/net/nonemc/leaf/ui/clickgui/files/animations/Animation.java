package net.nonemc.leaf.ui.clickgui.files.animations;

import net.nonemc.leaf.ui.clickgui.files.normal.TimerUtil;

public abstract class Animation {
    public TimerUtil timerUtil = new TimerUtil();
    protected int duration;
    protected double endPoint;
    protected Direction direction;
    public Animation(int ms, double endPoint) {
        this.duration = ms;
        this.endPoint = endPoint;
        this.direction = Direction.FORWARDS;
    }
    public Animation(int ms, double endPoint, Direction direction) {
        this.duration = ms;
        this.endPoint = endPoint;
        this.direction = direction;
    }
    public void reset() {
        timerUtil.reset();
    }
    public Direction getDirection() {
        return direction;
    }
    public void setDirection(Direction direction) {
        if (this.direction != direction) {
            this.direction = direction;
            timerUtil.setTime(System.currentTimeMillis() - (duration - Math.min(duration, timerUtil.getTime())));
        }
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
}
