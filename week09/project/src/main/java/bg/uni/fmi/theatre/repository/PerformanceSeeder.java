package bg.uni.fmi.theatre.repository;

import bg.uni.fmi.theatre.domain.Hall;
import bg.uni.fmi.theatre.domain.Performance;
import bg.uni.fmi.theatre.domain.Show;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Order(3)
public class PerformanceSeeder implements CommandLineRunner {

    private final PerformanceRepository performances;
    private final ShowRepository shows;
    private final HallRepository halls;

    public PerformanceSeeder(PerformanceRepository performances, ShowRepository shows, HallRepository halls) {
        this.performances = performances;
        this.shows = shows;
        this.halls = halls;
    }

    @Override
    public void run(String... args) {
        if (performances.count() > 0) return;

        Show hamlet = shows.findAll().stream()
                .filter(s -> s.getTitle().equals("Hamlet"))
                .findFirst().orElseThrow();
        Show chicago = shows.findAll().stream()
                .filter(s -> s.getTitle().equals("Chicago"))
                .findFirst().orElseThrow();
        Show swanLake = shows.findAll().stream()
                .filter(s -> s.getTitle().equals("Swan Lake"))
                .findFirst().orElseThrow();

        Hall mainStage = halls.findAll().stream()
                .filter(h -> h.getName().equals("Main Stage"))
                .findFirst().orElseThrow();
        Hall studio = halls.findAll().stream()
                .filter(h -> h.getName().equals("Studio Theatre"))
                .findFirst().orElseThrow();
        Hall amphitheatre = halls.findAll().stream()
                .filter(h -> h.getName().equals("Open Air Amphitheatre"))
                .findFirst().orElseThrow();

        // Hamlet performances
        performances.save(new Performance(hamlet, mainStage,
                LocalDateTime.of(2026, 6, 15, 19, 0)));
        performances.save(new Performance(hamlet, mainStage,
                LocalDateTime.of(2026, 6, 22, 19, 0)));

        // Chicago performances
        performances.save(new Performance(chicago, mainStage,
                LocalDateTime.of(2026, 7, 10, 20, 0)));
        performances.save(new Performance(chicago, studio,
                LocalDateTime.of(2026, 7, 17, 19, 30)));

        // Swan Lake performances
        performances.save(new Performance(swanLake, amphitheatre,
                LocalDateTime.of(2026, 8, 1, 18, 0)));
        performances.save(new Performance(swanLake, mainStage,
                LocalDateTime.of(2026, 8, 15, 19, 0)));
    }
}