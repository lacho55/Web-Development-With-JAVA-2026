package bg.uni.fmi.theatre.service;

/**
 * Replaced by {@link ShowService} and {@link PerformanceService}.
 *
 * A single "CatalogueService" that owns both Show and Performance logic
 * is a god-class anti-pattern. Each service should own one domain concept.
 *
 * This class is intentionally NOT annotated with @Service so Spring does
 * not manage it. See ShowService.java and PerformanceService.java.
 */
@Deprecated
class CatalogueService {
    // Intentionally empty — see ShowService and PerformanceService
}
