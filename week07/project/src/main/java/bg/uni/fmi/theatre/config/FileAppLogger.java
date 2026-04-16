package bg.uni.fmi.theatre.config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Profile("prod")
public class FileAppLogger implements AppLogger {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final LogLevel configuredLevel;
    private final String logFilePath;

    public FileAppLogger(TheatreProperties properties) {
        this.configuredLevel = properties.getLogLevel();
        this.logFilePath = properties.getLogFile();
        new File(logFilePath).getParentFile().mkdirs();
    }

    @Override public void trace(String m) {
        if (LogLevel.TRACE.isEnabled(configuredLevel)) {
            write("TRACE", m);
        }
    }
    @Override public void debug(String m) {
        if (LogLevel.DEBUG.isEnabled(configuredLevel)) {
            write("DEBUG", m);
        }
    }
    @Override public void info(String m) {
        if (LogLevel.INFO.isEnabled(configuredLevel)) {
            write("INFO ", m);
        }
    }
    @Override public void error(String m) { write("ERROR", m); }
    @Override public void error(String m, Throwable t) { write("ERROR", m + " — " + t.getMessage()); }

    private void write(String level, String msg) {
        String line = String.format("[%s] [%s] %s%n", LocalDateTime.now().format(FMT), level, msg);
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFilePath, true))) {
            pw.print(line);
        } catch (IOException e) {
            System.err.println("Log write failed: " + e.getMessage());
        }
    }
}
