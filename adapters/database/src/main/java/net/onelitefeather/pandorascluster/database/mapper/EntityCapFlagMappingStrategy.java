package net.onelitefeather.pandorascluster.database.mapper;

import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.database.models.flag.LandEntityCapFlagEntity;
import net.onelitefeather.pandorascluster.dbo.flag.EntityCapFlagDBO;

import java.util.function.Function;

public final class EntityCapFlagMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return model -> {
            if (model == null) return null;
            if (!(model instanceof EntityCapFlagDBO entityCapFlagDBO)) return null;
            return new LandEntityCapFlag(entityCapFlagDBO.id(), entityCapFlagDBO.name(), entityCapFlagDBO.spawnLimit(), null);
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {
            if (model == null) return null;
            if (!(model instanceof LandEntityCapFlag landEntityCapFlag)) return null;
            return new LandEntityCapFlagEntity(landEntityCapFlag.getId(), landEntityCapFlag.getName(), landEntityCapFlag.getSpawnLimit(), null);
        };
    }

    public static EntityCapFlagMappingStrategy create() {
        return new EntityCapFlagMappingStrategy();
    }
}
