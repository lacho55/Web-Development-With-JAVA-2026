package bg.uni.fmi.theatre.web;

import bg.uni.fmi.theatre.vo.Genre;
import bg.uni.fmi.theatre.dto.*;
import bg.uni.fmi.theatre.service.ShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TASK: REST controller for Show resources.
 *
 * HINT: A controller has exactly one job — translate HTTP ↔ service calls.
 * It should never contain business logic, entity creation, or DTO mapping.
 * All of that belongs in ShowService.
 *
 * Notice that this controller only depends on ShowService.
 * PerformanceController only depends on PerformanceService.
 * This is the Single Responsibility Principle applied to controllers.
 */
@RestController
@RequestMapping("/api/shows")
@Tag(name = "Shows", description = "CRUD operations for theatre shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }


    @GetMapping
    public List<ShowResponse> listShows() {
        return showService.getAllShows();
    }

    @GetMapping("/{id}")
    public ShowResponse getShowById(@PathVariable(name = "id") Long id) {
        return showService.getShowById(id);
    }


//    @GetMapping
//    @Operation(summary = "List shows", description = "Search shows with optional title/genre filters and pagination")
//    @ApiResponse(responseCode = "200", description = "Paginated list of shows")
//    public PageResponse<ShowResponse> listShows(
//            @Parameter(description = "Title substring filter (case-insensitive)")
//            @RequestParam(defaultValue = "") String title,
//            @Parameter(description = "Filter by genre")
//            @RequestParam(required = false) Genre genre,
//            @Parameter(description = "Page number (zero-based)")
//            @RequestParam(defaultValue = "0") int page,
//            @Parameter(description = "Page size")
//            @RequestParam(defaultValue = "10") int size) {
//        return showService.searchShows(title, genre, page, size);
//    }
//
//    @GetMapping("/{id}")
//    @Operation(summary = "Get show by ID")
//    @ApiResponse(responseCode = "200", description = "Show found")
//    @ApiResponse(responseCode = "404", description = "Show not found")
//    public ShowResponse getShow(@PathVariable Long id) {
//        return showService.getShowById(id);
//    }
//
//    @PostMapping
//    @Operation(summary = "Create a new show")
//    @ApiResponse(responseCode = "201", description = "Show created")
//    @ApiResponse(responseCode = "400", description = "Validation error")
//    public ResponseEntity<ShowResponse> createShow(@Valid @RequestBody ShowRequest req) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(showService.addShow(req));
//    }
//
//    @PutMapping("/{id}")
//    @Operation(summary = "Update an existing show")
//    @ApiResponse(responseCode = "200", description = "Show updated")
//    @ApiResponse(responseCode = "404", description = "Show not found")
//    @ApiResponse(responseCode = "400", description = "Validation error")
//    public ShowResponse updateShow(@PathVariable Long id, @Valid @RequestBody ShowRequest req) {
//        return showService.updateShow(id, req);
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Delete a show")
//    @ApiResponse(responseCode = "204", description = "Show deleted")
//    @ApiResponse(responseCode = "404", description = "Show not found")
//    public ResponseEntity<Void> deleteShow(@PathVariable Long id) {
//        showService.deleteShow(id);
//        return ResponseEntity.noContent().build();
//    }
}
