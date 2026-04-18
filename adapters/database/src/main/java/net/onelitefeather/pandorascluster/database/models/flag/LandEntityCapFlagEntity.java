package net.onelitefeather.pandorascluster.database.models.flag;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Entity
@Table(
        name = "entityCap_flags",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_entitycap_flags_container_name",
                columnNames = {"flagContainer_id", "name"}
        )
)
public final class LandEntityCapFlagEntity implements PandorasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "spawn_limit", nullable = false)
    private Integer spawnLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flagContainer_id", nullable = false)
    private FlagContainerEntity flagContainer;


    public LandEntityCapFlagEntity() {
        // Empty constructor for Hibernate
    }

    public LandEntityCapFlagEntity(Long id, String name, Integer spawnLimit, FlagContainerEntity flagContainer) {
        this.id = id;
        this.name = name;
        this.spawnLimit = spawnLimit;
        this.flagContainer = flagContainer;
    }


    public @Nullable Long id() {
        return this.id;
    }

    public @NotNull String name() {
        return this.name;
    }

    public Integer spawnLimit() {
        return this.spawnLimit;
    }

    public FlagContainerEntity flagContainer() {
        return this.flagContainer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LandEntityCapFlagEntity that)) return false;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? System.identityHashCode(this) : Objects.hashCode(id);
    }
}
