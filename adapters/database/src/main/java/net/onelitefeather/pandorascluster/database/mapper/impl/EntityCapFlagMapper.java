package net.onelitefeather.pandorascluster.database.mapper.impl;

import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper;
import net.onelitefeather.pandorascluster.database.models.flag.LandEntityCapFlagEntity;
import net.onelitefeather.pandorascluster.dbo.flag.EntityCapFlagDBO;
import org.jetbrains.annotations.Nullable;

public final class EntityCapFlagMapper implements DatabaseEntityMapper<EntityCapFlagDBO, LandEntityCapFlag> {

    @Override
    public @Nullable LandEntityCapFlag entityToModel(@Nullable EntityCapFlagDBO entity) {
        if (entity == null) return null;
        return new LandEntityCapFlag(entity.id(), entity.name(), entity.spawnLimit());
    }

    @Override
    public @Nullable EntityCapFlagDBO modelToEntity(@Nullable LandEntityCapFlag model) {
        if (model == null) return null;
        return new LandEntityCapFlagEntity(model.getId(), model.getName(), model.getSpawnLimit());
    }
}
