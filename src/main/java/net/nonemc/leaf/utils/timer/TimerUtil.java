package net.nonemc.leaf.utils.timer;

public class TimerUtil {
    public long lastMS;
    private long time;

    private long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public void setTime(long time) {
        lastMS = time;
    }

    public long time() {
        return System.nanoTime() / 1000000L - time;
    }

    public void reset() {
        this.lastMS = this.getCurrentMS();
    }

    public boolean delay(float milliSec) {
        return (float) (this.getTime() - this.lastMS) >= milliSec;
    }

    public long getTime() {
        return System.nanoTime() / 1000000L;
    }
}

