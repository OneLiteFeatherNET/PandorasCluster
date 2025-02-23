package net.onelitefeather.pandorascluster.database.mapper.impl;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;
import net.onelitefeather.pandorascluster.database.models.position.HomePositionEntity;
import net.onelitefeather.pandorascluster.dbo.land.LandDBO;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class LandMapper implements DatabaseEntityMapper<LandDBO, Land> {

    private final LandPlayerMapper playerMapper;
    private final FlagContainerMapper flagContainerMapper;
    private final LandAreaMapper landAreaMapper;
    private final HomePositionMapper homePositionMapper;

    public LandMapper(LandAreaMapper landAreaMapper,
                      FlagContainerMapper flagContainerMapper,
                      LandPlayerMapper landPlayerMapper) {

        this.landAreaMapper = landAreaMapper;
        this.flagContainerMapper = flagContainerMapper;
        this.playerMapper = landPlayerMapper;
        this.homePositionMapper = new HomePositionMapper();
    }

    @Override
    public @Nullable Land entityToModel(@Nullable LandDBO entity) {
        if (entity == null) return null;

        LandPlayer owner = this.playerMapper.entityToModel(entity.owner());
        var areas = entity.areas().stream().map(this.landAreaMapper::entityToModel).toList();

        FlagContainer flagContainer = this.flagContainerMapper.entityToModel(entity.flagContainer());

        return new Land(entity.id(), owner, this.homePositionMapper.entityToModel(entity.home()), areas, flagContainer);
    }

    @Override
    public @Nullable LandDBO modelToEntity(@Nullable Land model) {
        if (model == null) return null;

        LandPlayerEntity owner = (LandPlayerEntity) playerMapper.modelToEntity(model.getOwner());

        List<LandAreaEntity> areas = model.getAreas()
                .stream()
                .map(landArea -> (LandAreaEntity) this.landAreaMapper.modelToEntity(landArea))
                .toList();

        HomePositionEntity home = (HomePositionEntity) this.homePositionMapper.modelToEntity(model.getHome());
        FlagContainerEntity flagContainerEntity = (FlagContainerEntity) this.flagContainerMapper.modelToEntity(model.getFlagContainer());

        return new LandEntity(model.getId(), owner, home, areas, flagContainerEntity);
    }

    public HomePositionMapper getHomePositionMapper() {
        return homePositionMapper;
    }
}
