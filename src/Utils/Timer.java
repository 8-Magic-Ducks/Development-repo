package Utils;

public class Timer {
    private long startTime;
    private long duration;
    private boolean isRunning;

    public Timer(int seconds) {
        this.duration = seconds * 1000L;
        this.isRunning = false;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.isRunning = true;
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
        this.isRunning = true;
    }

    public boolean isTimeUp() {
        if (!isRunning) return false;
        return getRemaining() <= 0;
    }

    public long getRemaining() {
        if (!isRunning) return duration;
        long elapsed = System.currentTimeMillis() - startTime;
        long remaining = duration - elapsed;
        return Math.max(0, remaining);
    }
}