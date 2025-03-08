package net.onelitefeather.pandorascluster.database.mapper.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandNaturalFlagEntity;
import net.onelitefeather.pandorascluster.dbo.flag.NaturalFlagDBO;

import java.util.function.Function;

public final class NaturalFlagMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return entity -> {
            if (entity == null) return null;
            if (!(entity instanceof NaturalFlagDBO flag)) return null;

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(FlagContainerMappingStrategy.create());
            mappingContext.setMappingType(MapperType.ENTITY_TO_MODEL);

            FlagContainer flagContainer = (FlagContainer) mappingContext.doMapping(flag.flagContainer());

            return new LandNaturalFlag(flag.id(), flag.name(), flag.state(), flagContainer);
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {
            if (model == null) return null;
            if (!(model instanceof LandNaturalFlag flag)) return null;

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(FlagContainerMappingStrategy.create());
            mappingContext.setMappingType(MapperType.MODEL_TO_ENTITY);

            FlagContainerEntity flagContainer = (FlagContainerEntity) mappingContext.doMapping(flag.getParent());

            return new LandNaturalFlagEntity(flag.getId(), flag.getName(), flag.getState(), flagContainer);
        };
    }

    public static NaturalFlagMappingStrategy create() {
        return new NaturalFlagMappingStrategy();
    }
}
