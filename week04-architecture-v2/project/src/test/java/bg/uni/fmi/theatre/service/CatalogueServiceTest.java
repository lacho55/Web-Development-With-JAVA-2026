package bg.uni.fmi.theatre.service;

import bg.uni.fmi.theatre.domain.*;
import bg.uni.fmi.theatre.vo.*;
import bg.uni.fmi.theatre.repository.inmemory.InMemoryPerformanceRepository;
import bg.uni.fmi.theatre.repository.inmemory.InMemoryShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CatalogueServiceTest {
    private InMemoryShowRepository showRepo;
    private InMemoryPerformanceRepository perfRepo;
    private CatalogueService service;

    @BeforeEach
    void setUp() {
        showRepo = new InMemoryShowRepository();
        perfRepo = new InMemoryPerformanceRepository();
        service = new CatalogueService(showRepo, perfRepo, 5);
    }

    @Test
    void addShow_validShow_isSaved() {
        Show show = new Show(showRepo.nextId(), "Hamlet", "Classic drama", Genre.DRAMA, 120, AgeRating.PG_16);
        service.addShow(show);
        Optional<Show> found = service.findShowById(show.getId());
        assertTrue(found.isPresent());
        assertEquals("Hamlet", found.get().getTitle());
    }

    @Test
    void addShow_nullShow_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> service.addShow(null));
    }

    @Test
    void searchShows_byTitle_returnsMatchingShows() {
        service.addShow(new Show(showRepo.nextId(), "Hamlet", "Drama", Genre.DRAMA, 120, AgeRating.PG_16));
        service.addShow(new Show(showRepo.nextId(), "Othello", "Drama", Genre.DRAMA, 110, AgeRating.PG_16));
        service.addShow(new Show(showRepo.nextId(), "A Midsummer Night's Dream", "Comedy", Genre.COMEDY, 90, AgeRating.ALL));
        List<Show> results = service.searchShows("ham", null, 0, 10);
        assertEquals(1, results.size());
        assertEquals("Hamlet", results.get(0).getTitle());
    }

    @Test
    void searchShows_byGenre_returnsMatchingShows() {
        service.addShow(new Show(showRepo.nextId(), "Hamlet", "Drama", Genre.DRAMA, 120, AgeRating.PG_16));
        service.addShow(new Show(showRepo.nextId(), "Chicago", "Musical", Genre.MUSICAL, 130, AgeRating.PG_12));
        List<Show> results = service.searchShows(null, Genre.MUSICAL, 0, 10);
        assertEquals(1, results.size());
        assertEquals("Chicago", results.get(0).getTitle());
    }

    @Test
    void searchShows_caseInsensitive_returnsResults() {
        service.addShow(new Show(showRepo.nextId(), "Hamlet", "Drama", Genre.DRAMA, 120, AgeRating.PG_16));
        List<Show> results = service.searchShows("HAMLET", null, 0, 10);
        assertEquals(1, results.size());
    }

    @Test
    void searchShows_emptyQuery_returnsAllShows() {
        service.addShow(new Show(showRepo.nextId(), "Hamlet", "Drama", Genre.DRAMA, 120, AgeRating.PG_16));
        service.addShow(new Show(showRepo.nextId(), "Chicago", "Musical", Genre.MUSICAL, 130, AgeRating.PG_12));
        List<Show> results = service.searchShows("", null, 0, 10);
        assertEquals(2, results.size());
    }

    @Test
    void searchShows_pageOutOfBounds_returnsEmptyList() {
        service.addShow(new Show(showRepo.nextId(), "Hamlet", "Drama", Genre.DRAMA, 120, AgeRating.PG_16));
        List<Show> results = service.searchShows(null, null, 5, 10);
        assertTrue(results.isEmpty());
    }

    @Test
    void searchShows_negativePage_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> service.searchShows(null, null, -1, 10));
    }

    @Test
    void searchShows_zeroSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> service.searchShows(null, null, 0, 0));
    }

    @Test
    void searchShows_pagination_returnsCorrectPage() {
        for (int i = 1; i <= 8; i++) {
            service.addShow(new Show((long) i, "Show " + i, "Desc", Genre.DRAMA, 90, AgeRating.ALL));
        }
        List<Show> page0 = service.searchShows(null, null, 0, 3);
        List<Show> page1 = service.searchShows(null, null, 1, 3);
        List<Show> page2 = service.searchShows(null, null, 2, 3);
        assertEquals(3, page0.size());
        assertEquals(3, page1.size());
        assertEquals(2, page2.size());
    }

    @Test
    void addPerformance_unknownShow_throwsException() {
        Performance p = new Performance(1L, 999L, 1L, LocalDateTime.now().plusDays(1));
        assertThrows(IllegalArgumentException.class, () -> service.addPerformance(p));
    }

    @Test
    void findPerformancesByShow_validShow_returnsPerformances() {
        Show show = new Show(showRepo.nextId(), "Hamlet", "Drama", Genre.DRAMA, 120, AgeRating.PG_16);
        service.addShow(show);
        service.addPerformance(new Performance(perfRepo.nextId(), show.getId(), 1L, LocalDateTime.now().plusDays(1)));
        service.addPerformance(new Performance(perfRepo.nextId(), show.getId(), 1L, LocalDateTime.now().plusDays(2)));
        List performances = service.findPerformancesByShow(show.getId());
        assertEquals(2, performances.size());
    }
}
