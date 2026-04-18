package net.onelitefeather.pandorascluster.database.mapper.flag;

import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.database.models.flag.LandNaturalFlagEntity;

public final class NaturalFlagMapper {

    private NaturalFlagMapper() {
    }

    public static LandNaturalFlag toModel(LandNaturalFlagEntity entity) {
        if (entity == null) return null;
        return new LandNaturalFlag(entity.id(), entity.name(), entity.state(), null);
    }

    public static LandNaturalFlagEntity toEntity(LandNaturalFlag model) {
        if (model == null) return null;
        return new LandNaturalFlagEntity(model.getId(), model.getName(), model.getState(), null);
    }
}
