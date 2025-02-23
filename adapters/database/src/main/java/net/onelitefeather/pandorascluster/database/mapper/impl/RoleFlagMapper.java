package net.onelitefeather.pandorascluster.database.mapper.impl;

import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper;
import net.onelitefeather.pandorascluster.database.models.flag.LandRoleFlagEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.dbo.flag.RoleFlagDBO;
import org.jetbrains.annotations.Nullable;

public class RoleFlagMapper implements DatabaseEntityMapper<RoleFlagDBO, LandRoleFlag> {
    private final LandMapper landMapper;

    public RoleFlagMapper(LandMapper landMapper) {
        this.landMapper = landMapper;
    }

    @Override
    public @Nullable LandRoleFlag entityToModel(@Nullable RoleFlagDBO entity) {
        if (entity == null) return null;
        return new LandRoleFlag(
                entity.id(),
                entity.name(),
                entity.state(),
                entity.role(),
                landMapper.entityToModel(entity.land()));
    }

    @Override
    public @Nullable RoleFlagDBO modelToEntity(@Nullable LandRoleFlag model) {
        if (model == null) return null;
        return new LandRoleFlagEntity(
                model.getId(),
                model.getName(),
                model.getState(),
                model.getRole(),
                (LandAreaEntity) landAreaMapper.modelToEntity(model.getLandArea()));
    }
}
