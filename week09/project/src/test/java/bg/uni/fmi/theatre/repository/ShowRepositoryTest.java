package bg.uni.fmi.theatre.repository;

import bg.uni.fmi.theatre.AbstractDatabaseTest;
import bg.uni.fmi.theatre.domain.Show;
import bg.uni.fmi.theatre.vo.AgeRating;
import bg.uni.fmi.theatre.vo.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShowRepositoryTest extends AbstractDatabaseTest {

    @Autowired
    private ShowRepository showRepository;

    @Test
    void save_and_findById_roundTrip() {
        Show show = new Show("Test Show", "A test description",
                Genre.COMEDY, 90, AgeRating.ALL);

        Show saved = showRepository.save(show);

        assertThat(saved.getId()).isNotNull();

        Optional<Show> found = showRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Show");
        assertThat(found.get().getGenre()).isEqualTo(Genre.COMEDY);
    }

    @Test
    void findAll_returnsAllSavedShows() {
        showRepository.save(new Show("Show A", "desc", Genre.DRAMA, 120, AgeRating.PG_16));
        showRepository.save(new Show("Show B", "desc", Genre.BALLET, 100, AgeRating.ALL));

        List<Show> all = showRepository.findAll();

        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
        assertThat(all).extracting(Show::getTitle).contains("Show A", "Show B");
    }

    @Test
    void deleteById_removesShow() {
        Show saved = showRepository.save(
                new Show("To Delete", "desc", Genre.OPERA, 150, AgeRating.PG_12));

        showRepository.deleteById(saved.getId());

        assertThat(showRepository.findById(saved.getId())).isEmpty();
    }
}