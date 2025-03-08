package net.onelitefeather.pandorascluster.database.mapper.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.database.mapper.land.LandMappingStrategy;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandEntityCapFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandNaturalFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandRoleFlagEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import net.onelitefeather.pandorascluster.dbo.flag.FlagContainerDBO;

import java.util.List;
import java.util.function.Function;

public final class FlagContainerMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return entity -> {
            if (entity == null) return null;
            if (!(entity instanceof FlagContainerDBO flagContainer)) return null;

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(LandMappingStrategy.create());
            mappingContext.setMappingType(MapperType.ENTITY_TO_MODEL);

            Land land = (Land) mappingContext.doMapping(flagContainer.land());

            List<LandNaturalFlag> naturalFlags = flagContainer.naturalFlags()
                    .stream()
                    .map(naturalFlagDBO -> (LandNaturalFlag) mappingContext.doMapping(naturalFlagDBO))
                    .toList();

            List<LandRoleFlag> roleFlags = flagContainer.roleFlags()
                    .stream()
                    .map(naturalFlagDBO -> (LandRoleFlag) mappingContext.doMapping(naturalFlagDBO))
                    .toList();

            List<LandEntityCapFlag> entityCapFlags = flagContainer.entityCapFlags()
                    .stream()
                    .map(naturalFlagDBO -> (LandEntityCapFlag) mappingContext.doMapping(naturalFlagDBO))
                    .toList();

            return new FlagContainer(flagContainer.id(), land, naturalFlags, roleFlags, entityCapFlags);
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {

            if (model == null) return null;
            if(!(model instanceof FlagContainer flagContainer)) return null;

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(LandMappingStrategy.create());
            mappingContext.setMappingType(MapperType.MODEL_TO_ENTITY);

            List<LandNaturalFlagEntity> naturalFlags = flagContainer.getNaturalFlags()
                    .stream()
                    .map(naturalFlagDBO -> (LandNaturalFlagEntity) mappingContext.doMapping(naturalFlagDBO))
                    .toList();

            List<LandRoleFlagEntity> roleFlags = flagContainer.getRoleFlags()
                    .stream()
                    .map(naturalFlagDBO -> (LandRoleFlagEntity) mappingContext.doMapping(naturalFlagDBO))
                    .toList();

            List<LandEntityCapFlagEntity> entityCapFlags = flagContainer.getEntityCapFlags()
                    .stream()
                    .map(naturalFlagDBO -> (LandEntityCapFlagEntity) mappingContext.doMapping(naturalFlagDBO))
                    .toList();

            LandEntity land = (LandEntity) mappingContext.doMapping(flagContainer.getLand());

            return new FlagContainerEntity(flagContainer.getId(), land, naturalFlags, roleFlags, entityCapFlags);
        };
    }

    public static FlagContainerMappingStrategy create() {
        return new FlagContainerMappingStrategy();
    }
}
