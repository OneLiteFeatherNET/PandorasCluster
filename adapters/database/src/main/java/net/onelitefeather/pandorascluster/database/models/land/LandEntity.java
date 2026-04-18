package net.onelitefeather.pandorascluster.database.models.land;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;
import net.onelitefeather.pandorascluster.database.models.position.HomePositionEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "lands")
public final class LandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private LandPlayerEntity owner;

    @ManyToOne
    @JoinColumn(name = "home_id", nullable = false)
    private HomePositionEntity home;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "land")
    private List<LandAreaEntity> areas;

    @OneToOne
    @JoinColumn(name = "flag_container_id", nullable = false)
    private FlagContainerEntity flagContainerEntity;

    public LandEntity() {
        // Empty constructor for Hibernate
    }

    public LandEntity(Long id,
                      LandPlayerEntity owner,
                      HomePositionEntity home,
                      List<LandAreaEntity> areas,
                      FlagContainerEntity flagContainerEntity) {
        this.id = id;
        this.owner = owner;
        this.home = home;
        this.areas = areas;
        this.flagContainerEntity = flagContainerEntity;
    }

    public @Nullable Long id() {
        return id;
    }

    public @NotNull LandPlayerEntity owner() {
        return owner;
    }

    public @NotNull HomePositionEntity home() {
        return home;
    }

    public @NotNull List<LandAreaEntity> areas() {
        return Collections.unmodifiableList(areas);
    }

    public FlagContainerEntity flagContainer() {
        return this.flagContainerEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LandEntity that)) return false;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? System.identityHashCode(this) : Objects.hashCode(id);
    }
}
