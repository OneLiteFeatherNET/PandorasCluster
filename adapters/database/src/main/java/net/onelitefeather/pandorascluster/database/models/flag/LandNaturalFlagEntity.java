package net.onelitefeather.pandorascluster.database.models.flag;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Entity
@Table(
        name = "natural_flags",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_natural_flags_container_name",
                columnNames = {"flagContainer_id", "name"}
        )
)
public final class LandNaturalFlagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "state", nullable = false)
    private boolean state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flagContainer_id", nullable = false)
    private FlagContainerEntity flagContainer;

    public LandNaturalFlagEntity() {
        // Empty constructor for Hibernate
    }

    public LandNaturalFlagEntity(Long id, String name, boolean state, FlagContainerEntity flagContainer) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.flagContainer = flagContainer;
    }

    public @Nullable Long id() {
        return this.id;
    }

    public @NotNull String name() {
        return this.name;
    }

    public boolean state() {
        return this.state;
    }

    public FlagContainerEntity flagContainer() {
        return flagContainer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LandNaturalFlagEntity that)) return false;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? System.identityHashCode(this) : Objects.hashCode(id);
    }
}
