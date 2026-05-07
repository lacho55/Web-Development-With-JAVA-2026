package bg.uni.fmi.theatre.repository;

import bg.uni.fmi.theatre.domain.Hall;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class HallSeeder implements CommandLineRunner {

    private final HallRepository halls;

    public HallSeeder(HallRepository halls) {
        this.halls = halls;
    }

    @Override
    public void run(String... args) {
        if (halls.count() > 0) return;

        halls.save(new Hall("Main Stage", 500));
        halls.save(new Hall("Studio Theatre", 120));
        halls.save(new Hall("Open Air Amphitheatre", 800));
    }
}