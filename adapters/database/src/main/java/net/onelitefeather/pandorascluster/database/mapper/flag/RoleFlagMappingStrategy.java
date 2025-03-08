package net.onelitefeather.pandorascluster.database.mapper.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandRoleFlagEntity;
import net.onelitefeather.pandorascluster.dbo.flag.RoleFlagDBO;

import java.util.function.Function;

public final class RoleFlagMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return entity -> {
            if (entity == null) return null;
            if (!(entity instanceof RoleFlagDBO flag)) return null;

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(FlagContainerMappingStrategy.create());
            mappingContext.setMappingType(MapperType.ENTITY_TO_MODEL);

            FlagContainer flagContainer = (FlagContainer) mappingContext.doMapping(flag.flagContainer());
            if (flagContainer == null) return null;

            return new LandRoleFlag(flag.id(), flag.name(), flag.state(), flag.role(), flagContainer);
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {
            if (model == null) return null;
            if (!(model instanceof LandRoleFlag flag)) return null;

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(FlagContainerMappingStrategy.create());
            mappingContext.setMappingType(MapperType.MODEL_TO_ENTITY);

            FlagContainerEntity flagContainer = (FlagContainerEntity) mappingContext.doMapping(flag.getParent());

            return new LandRoleFlagEntity(flag.getId(), flag.getName(), flag.getState(), flag.getRole(), flagContainer);
        };
    }

    public static RoleFlagMappingStrategy create() {
        return new RoleFlagMappingStrategy();
    }
}
