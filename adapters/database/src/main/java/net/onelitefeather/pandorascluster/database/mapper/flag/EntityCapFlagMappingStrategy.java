package net.onelitefeather.pandorascluster.database.mapper.flag;

import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.database.models.flag.LandEntityCapFlagEntity;

import java.util.function.Function;

/**
 * The flag↔flagContainer back-reference is intentionally left null to avoid
 * the mapper cycle (FlagContainer → entityCapFlags → entityCapFlag.parent → …).
 */
public final class EntityCapFlagMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return entity -> {
            if (entity == null) return null;
            if (!(entity instanceof LandEntityCapFlagEntity flag)) return null;
            return new LandEntityCapFlag(flag.id(), flag.name(), flag.spawnLimit(), null);
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {
            if (model == null) return null;
            if (!(model instanceof LandEntityCapFlag flag)) return null;
            return new LandEntityCapFlagEntity(flag.getId(), flag.getName(), flag.getSpawnLimit(), null);
        };
    }

    public static EntityCapFlagMappingStrategy create() {
        return new EntityCapFlagMappingStrategy();
    }
}
