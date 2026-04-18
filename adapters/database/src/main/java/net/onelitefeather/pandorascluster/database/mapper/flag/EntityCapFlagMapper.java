package net.onelitefeather.pandorascluster.database.mapper.flag;

import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.database.models.flag.LandEntityCapFlagEntity;

public final class EntityCapFlagMapper {

    private EntityCapFlagMapper() {
    }

    public static LandEntityCapFlag toModel(LandEntityCapFlagEntity entity) {
        if (entity == null) return null;
        return new LandEntityCapFlag(entity.id(), entity.name(), entity.spawnLimit(), null);
    }

    public static LandEntityCapFlagEntity toEntity(LandEntityCapFlag model) {
        if (model == null) return null;
        return new LandEntityCapFlagEntity(model.getId(), model.getName(), model.getSpawnLimit(), null);
    }
}
