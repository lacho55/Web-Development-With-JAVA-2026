package bg.uni.fmi.theatre.config;

public enum LogLevel {
    TRACE(0), DEBUG(1), INFO(2), ERROR(3);

    private final int priority;

    LogLevel(int priority) {
        this.priority = priority;
    }

    public boolean isEnabled(LogLevel configured) {
        return this.priority >= configured.priority;
    }
}
