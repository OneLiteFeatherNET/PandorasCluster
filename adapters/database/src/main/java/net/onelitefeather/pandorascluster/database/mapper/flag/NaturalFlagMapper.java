package net.onelitefeather.pandorascluster.database.mapper.flag;

import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.WorldNaturalFlag;
import net.onelitefeather.pandorascluster.database.models.flag.LandNaturalFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.WorldNaturalFlagEntity;

public final class NaturalFlagMapper {

    private NaturalFlagMapper() {
    }

    public static LandNaturalFlag toModel(LandNaturalFlagEntity entity) {
        if (entity == null) return null;
        return new LandNaturalFlag(entity.id(), entity.name(), entity.state(), null);
    }

    public static LandNaturalFlagEntity toEntity(LandNaturalFlag model) {
        if (model == null) return null;
        return new LandNaturalFlagEntity(model.id(), model.name(), model.state(), null);
    }

    public static WorldNaturalFlag toModel(WorldNaturalFlagEntity entity) {
        if (entity == null) return null;
        return new WorldNaturalFlag(entity.id(), entity.name(), entity.state(), null);
    }

    public static WorldNaturalFlagEntity toEntity(WorldNaturalFlag model) {
        if (model == null) return null;
        return new WorldNaturalFlagEntity(model.id(), model.name(), model.state(), null);
    }
}
