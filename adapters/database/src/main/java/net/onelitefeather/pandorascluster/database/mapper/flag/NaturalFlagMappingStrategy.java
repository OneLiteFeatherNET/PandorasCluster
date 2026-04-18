package net.onelitefeather.pandorascluster.database.mapper.flag;

import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.database.models.flag.LandNaturalFlagEntity;

import java.util.function.Function;

/**
 * The flag↔flagContainer back-reference is intentionally left null to avoid
 * the mapper cycle (FlagContainer → naturalFlags → naturalFlag.parent → …).
 */
public final class NaturalFlagMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return entity -> {
            if (entity == null) return null;
            if (!(entity instanceof LandNaturalFlagEntity flag)) return null;
            return new LandNaturalFlag(flag.id(), flag.name(), flag.state(), null);
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {
            if (model == null) return null;
            if (!(model instanceof LandNaturalFlag flag)) return null;
            return new LandNaturalFlagEntity(flag.getId(), flag.getName(), flag.getState(), null);
        };
    }

    public static NaturalFlagMappingStrategy create() {
        return new NaturalFlagMappingStrategy();
    }
}
