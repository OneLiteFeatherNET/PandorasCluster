package net.onelitefeather.pandorascluster.database.mapper.flag;

import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.database.models.flag.LandRoleFlagEntity;

import java.util.function.Function;

/**
 * The flag↔flagContainer back-reference is intentionally left null to avoid
 * the mapper cycle (FlagContainer → roleFlags → roleFlag.parent → …).
 * Callers that need the parent container must look it up separately.
 */
public final class RoleFlagMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return entity -> {
            if (entity == null) return null;
            if (!(entity instanceof LandRoleFlagEntity flag)) return null;
            return new LandRoleFlag(flag.id(), flag.name(), flag.state(), flag.role(), null);
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {
            if (model == null) return null;
            if (!(model instanceof LandRoleFlag flag)) return null;
            return new LandRoleFlagEntity(flag.getId(), flag.getName(), flag.getState(), flag.getRole(), null);
        };
    }

    public static RoleFlagMappingStrategy create() {
        return new RoleFlagMappingStrategy();
    }
}
