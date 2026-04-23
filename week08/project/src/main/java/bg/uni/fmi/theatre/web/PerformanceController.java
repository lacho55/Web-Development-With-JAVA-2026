package bg.uni.fmi.theatre.web;

import bg.uni.fmi.theatre.dto.PerformanceResponse;
import bg.uni.fmi.theatre.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Performance resources.
 *
 * @since Week 06, Task 1
 */
@RestController
@RequestMapping("/api/performances")
@Tag(name = "Performances", description = "Query scheduled performances")
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @GetMapping
    @Operation(summary = "List performances", description = "List all performances, optionally filtered by show ID")
    @ApiResponse(responseCode = "200", description = "List of performances")
    @ApiResponse(responseCode = "404", description = "Show not found (when showId is provided but invalid)")
    public List<PerformanceResponse> listPerformances(
            @Parameter(description = "Filter by show ID")
            @RequestParam(required = false) Long showId) {
        return showId != null
                ? performanceService.findPerformancesByShow(showId)
                : performanceService.getAllPerformances();
    }
}
