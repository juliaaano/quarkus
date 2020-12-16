package app.pet.db;

import static java.time.Instant.now;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@MappedSuperclass
abstract class Entity extends PanacheEntityBase {

    @Column(name = "created_at")
    public Instant createdAt;

    @Column(name = "updated_at")
    public Instant updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = now();
        this.updatedAt = createdAt;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = now();
    }
}
