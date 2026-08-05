package net.onelitefeather.pandorascluster.database.models.flag;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.api.land.flag.WorldNaturalFlag;
import net.onelitefeather.pandorascluster.database.models.land.LandWorldEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Entity
@Table(
        name = "world_natural_flags",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_world_natural_flags_name",
                columnNames = {"world_id", "name"}
        )
)
public class WorldNaturalFlagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "state", nullable = false)
    private boolean state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "world_id", nullable = false)
    private LandWorldEntity world;

    public WorldNaturalFlagEntity() {
        //Default Constructor
    }

    public WorldNaturalFlagEntity(Long id, String name, boolean state, LandWorldEntity world) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.world = world;
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

    public LandWorldEntity world() {
        return this.world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldNaturalFlagEntity that)) return false;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? System.identityHashCode(this) : Objects.hashCode(id);
    }
}
