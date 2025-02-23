package net.onelitefeather.pandorascluster.database.models.land;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;
import net.onelitefeather.pandorascluster.database.models.position.HomePositionEntity;
import net.onelitefeather.pandorascluster.dbo.flag.FlagContainerDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandAreaDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandDBO;
import net.onelitefeather.pandorascluster.dbo.player.LandPlayerDBO;
import net.onelitefeather.pandorascluster.dbo.position.HomePositionDBO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "lands")
public class LandEntity implements LandDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private LandPlayerEntity owner;

    @OneToOne
    private HomePositionEntity home;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "land")
    private List<LandAreaEntity> areas;

    @OneToOne
    private FlagContainerEntity flagContainerEntity;

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
    public @NotNull LandPlayerDBO owner() {
        return owner;
    }

    @Override
    public @NotNull HomePositionDBO home() {
        return home;
    }

    @Override
    public @NotNull List<LandAreaDBO> areas() {
        return Collections.unmodifiableList(areas);
    }

    @Override
    public FlagContainerDBO flagContainer() {
        return this.flagContainerEntity;
    }
}
