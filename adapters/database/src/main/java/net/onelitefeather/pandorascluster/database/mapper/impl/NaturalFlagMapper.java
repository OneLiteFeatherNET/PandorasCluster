package net.onelitefeather.pandorascluster.database.mapper.impl;

import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper;
import net.onelitefeather.pandorascluster.database.models.flag.LandNaturalFlagEntity;
import net.onelitefeather.pandorascluster.dbo.flag.NaturalFlagDBO;
import org.jetbrains.annotations.Nullable;

public class NaturalFlagMapper implements DatabaseEntityMapper<NaturalFlagDBO, LandNaturalFlag> {

    @Override
    public @Nullable LandNaturalFlag entityToModel(@Nullable NaturalFlagDBO entity) {
        if (entity == null) return null;
        return new LandNaturalFlag(entity.id(), entity.name(), entity.state());
    }

    @Override
    public @Nullable NaturalFlagDBO modelToEntity(@Nullable LandNaturalFlag model) {
        if (model == null) return null;
        return new LandNaturalFlagEntity(model.getId(), model.getName(), model.getState());
    }
}
