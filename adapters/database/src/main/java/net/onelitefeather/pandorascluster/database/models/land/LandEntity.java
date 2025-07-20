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

@Entity
@Table(name = "lands")
public final class LandEntity implements LandDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private LandPlayerEntity owner;

    @ManyToOne
    private HomePositionEntity home;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "land")
    private List<LandAreaEntity> areas;

    @OneToOne
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
}
