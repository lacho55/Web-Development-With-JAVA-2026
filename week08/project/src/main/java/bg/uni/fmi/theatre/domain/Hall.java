package bg.uni.fmi.theatre.domain;

import java.util.Objects;

public class Hall {
    private final Long id;
    private String name;

    public Hall(Long id, String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hall h)) return false;
        return Objects.equals(id, h.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
    @Override public String toString() { return "Hall{id=" + id + ", name='" + name + "'}"; }
}
