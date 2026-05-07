package bg.uni.fmi.theatre.domain;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "hall")
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int capacity;

    protected Hall() {}  // JPA requires a no-args constructor

    public Hall(String name, int capacity) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        this.name = name;
        this.capacity = capacity;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public void setName(String name) { this.name = name; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hall h)) return false;
        return Objects.equals(id, h.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
    @Override public String toString() { return "Hall{id=" + id + ", name='" + name + "', capacity=" + capacity + "}"; }
}
