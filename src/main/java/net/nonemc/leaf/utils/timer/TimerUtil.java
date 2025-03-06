package net.nonemc.leaf.utils.timer;

public class TimerUtil {
    public long lastMS;
    private long time;
    private long prevTime;

    private long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public boolean hasReached(double milliseconds) {
        return (double) (this.getCurrentMS() - this.lastMS) >= milliseconds;
    }

    public boolean hasReached(long delay) {
        return System.currentTimeMillis() - this.lastMS >= delay;
    }

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
    }

    public boolean hasPassed(double milli) {
        return System.currentTimeMillis() - this.prevTime >= milli;
    }

    public boolean sleep(final long time) {
        if (time() >= time) {
            reset();
            return true;
        }
        return false;
    }

    public long time() {
        return System.nanoTime() / 1000000L - time;
    }

    public final long getElapsedTime() {
        return this.getCurrentMS() - this.lastMS;
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

    public void setTime(long time) {
        lastMS = time;
    }

    public boolean isDelayComplete(long delay) {
        return System.currentTimeMillis() - this.lastMS > delay;
    }
}

