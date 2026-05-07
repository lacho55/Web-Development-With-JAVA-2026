package bg.uni.fmi.theatre.dto;

import java.util.List;

/**
 * Generic paginated response envelope wrapping a content slice with pagination metadata.
 *
 * @param <T> type of element in the page
 * @since Week 06, Task 4
 * @see bg.uni.fmi.theatre.service.ShowService#searchShows(String, bg.uni.fmi.theatre.vo.Genre, int, int)
 */
public class PageResponse<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;

    public PageResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
    }

    public List<T> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
}
