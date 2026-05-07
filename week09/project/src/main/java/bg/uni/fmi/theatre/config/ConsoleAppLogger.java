package bg.uni.fmi.theatre.config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Profile("dev")
public class ConsoleAppLogger implements AppLogger {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final LogLevel configuredLevel;

    public ConsoleAppLogger(TheatreProperties properties) {
        this.configuredLevel = properties.getLogLevel();
    }

    @Override public void trace(String m) {
        if (LogLevel.TRACE.isEnabled(configuredLevel)) {
            print("TRACE", m);
        }
    }
    @Override public void debug(String m) {
        if (LogLevel.DEBUG.isEnabled(configuredLevel)) {
            print("DEBUG", m);
        }
    }
    @Override public void info(String m) {
        if (LogLevel.INFO.isEnabled(configuredLevel)) {
            print("INFO ", m);
        }
    }
    @Override public void error(String m) { print("ERROR", m); }
    @Override public void error(String m, Throwable t) { print("ERROR", m + " — " + t.getMessage()); }

    private void print(String level, String msg) {
        System.out.printf("[%s] [%s] %s%n", LocalDateTime.now().format(FMT), level, msg);
    }
}
