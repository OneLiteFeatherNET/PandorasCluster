package net.onelitefeather.pandorascluster.database.models.land;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;
import net.onelitefeather.pandorascluster.database.models.position.HomePositionEntity;
import net.onelitefeather.pandorascluster.dto.flag.FlagContainerDto;
import net.onelitefeather.pandorascluster.dto.land.LandAreaDto;
import net.onelitefeather.pandorascluster.dto.land.LandDto;
import net.onelitefeather.pandorascluster.dto.player.LandPlayerDto;
import net.onelitefeather.pandorascluster.dto.position.HomePositionDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "lands")
public final class LandEntity implements LandDto {

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

    @Override
    public @Nullable Long id() {
        return id;
    }

    @Override
    public @NotNull LandPlayerDto owner() {
        return owner;
    }

    @Override
    public @NotNull HomePositionDto home() {
        return home;
    }

    @Override
    public @NotNull List<LandAreaDto> areas() {
        return Collections.unmodifiableList(areas);
    }

    @Override
    public FlagContainerDto flagContainer() {
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
