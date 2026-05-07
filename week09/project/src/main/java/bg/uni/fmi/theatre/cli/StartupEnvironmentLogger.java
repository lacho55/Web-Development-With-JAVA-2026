package bg.uni.fmi.theatre.cli;

import bg.uni.fmi.theatre.config.AppLogger;
import bg.uni.fmi.theatre.config.TheatreProperties;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class StartupEnvironmentLogger implements ApplicationRunner {
    private final AppLogger logger;
    private final TheatreProperties properties;
    private final ApplicationContext ctx;

    public StartupEnvironmentLogger(AppLogger logger, TheatreProperties properties, ApplicationContext ctx) {
        this.logger = logger;
        this.properties = properties;
        this.ctx = ctx;
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("=== Theatre App Started ===");
        logger.info("Log level     : " + properties.getLogLevel());
        logger.info("Page size     : " + properties.getDefaultPageSize());
        logger.info("Reservation   : " + properties.getReservationHoldMinutes() + " min hold");
        logger.info("Log file      : " + properties.getLogFile());
        logger.debug("--- Project beans (bg.uni.fmi.theatre) ---");
        Arrays.stream(ctx.getBeanDefinitionNames())
                .filter(name -> {
                    try {
                        return ctx.getBean(name).getClass().getPackageName().startsWith("bg.uni.fmi.theatre");
                    } catch (Exception e) { return false; }
                })
                .forEach(name -> logger.debug("  bean: " + name));
    }
}
