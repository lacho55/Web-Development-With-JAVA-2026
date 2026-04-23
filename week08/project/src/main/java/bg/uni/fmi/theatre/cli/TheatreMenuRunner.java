package bg.uni.fmi.theatre.cli;

import bg.uni.fmi.theatre.config.AppLogger;
import bg.uni.fmi.theatre.config.TheatreProperties;
import bg.uni.fmi.theatre.exception.*;
import bg.uni.fmi.theatre.service.PerformanceService;
import bg.uni.fmi.theatre.service.ShowService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * HINT: The CLI runner now depends on ShowService and PerformanceService separately.
 * Notice how natural this is — when you list performances for a show, you ask
 * PerformanceService (not ShowService) because that's the right domain boundary.
 */
@Component
public class TheatreMenuRunner implements CommandLineRunner {

    private final ShowService showService;
    private final PerformanceService performanceService;
    private final AppLogger logger;
    private final TheatreProperties properties;

    public TheatreMenuRunner(ShowService showService,
                              PerformanceService performanceService,
                              AppLogger logger,
                              TheatreProperties properties) {
        this.showService = showService;
        this.performanceService = performanceService;
        this.logger = logger;
        this.properties = properties;
    }

    @Override
    public void run(String... args) {
        logger.info("Theatre CLI started — page size: " + properties.getDefaultPageSize()
                + ", hold: " + properties.getReservationHoldMinutes() + "min");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        System.out.println("\n=== Theatre Ticketing System (Week 06 — REST API) ===");

        while (running) {
            System.out.println("\n1. List shows | 2. Search | 3. Performances | 0. Exit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    var shows = showService.getAllShows();
                    if (shows.isEmpty()) System.out.println("No shows found.");
                    else shows.forEach(s -> System.out.printf("  [%d] %s (%s)%n", s.getId(), s.getTitle(), s.getGenre()));
                }
                case "2" -> {
                    System.out.print("Title search: ");
                    String q = scanner.nextLine().trim();
                    try {
                        showService.searchShows(q, null, 0, properties.getDefaultPageSize())
                                .getContent()
                                .forEach(s -> System.out.printf("  [%d] %s%n", s.getId(), s.getTitle()));
                    } catch (ValidationException e) { System.out.println("Validation: " + e.getMessage()); }
                }
                case "3" -> {
                    System.out.print("Show ID: ");
                    try {
                        Long id = Long.parseLong(scanner.nextLine().trim());
                        showService.getShowById(id); // validates show exists
                        performanceService.findPerformancesByShow(id)
                                .forEach(p -> System.out.printf("  [%d] %s%n", p.getId(), p.getStartTime()));
                    } catch (NumberFormatException e) { System.out.println("Invalid ID."); }
                      catch (NotFoundException e) { System.out.println("Not found: " + e.getMessage()); }
                }
                case "0" -> { running = false; logger.info("CLI exiting."); System.out.println("Goodbye!"); }
                default -> System.out.println("Unknown option.");
            }
        }
    }
}
