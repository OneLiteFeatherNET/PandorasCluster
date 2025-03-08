package net.onelitefeather.pandorascluster.database.mapper.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandEntityCapFlagEntity;
import net.onelitefeather.pandorascluster.dbo.flag.EntityCapFlagDBO;

import java.util.function.Function;

public final class EntityCapFlagMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return entity -> {
            if (entity == null) return null;
            if (!(entity instanceof EntityCapFlagDBO flag)) return null;

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(FlagContainerMappingStrategy.create());
            mappingContext.setMappingType(MapperType.ENTITY_TO_MODEL);

            FlagContainer flagContainer = (FlagContainer) mappingContext.doMapping(flag.flagContainer());

            return new LandEntityCapFlag(flag.id(), flag.name(), flag.spawnLimit(), flagContainer);
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {
            if (model == null) return null;
            if (!(model instanceof LandEntityCapFlag flag)) return null;

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(FlagContainerMappingStrategy.create());
            mappingContext.setMappingType(MapperType.MODEL_TO_ENTITY);

            FlagContainerEntity flagContainer = (FlagContainerEntity) mappingContext.doMapping(flag.getParent());
            return new LandEntityCapFlagEntity(flag.getId(), flag.getName(), flag.getSpawnLimit(), flagContainer);
        };
    }

    public static EntityCapFlagMappingStrategy create() {
        return new EntityCapFlagMappingStrategy();
    }
}
