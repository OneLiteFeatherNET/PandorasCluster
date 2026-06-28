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
import net.onelitefeather.pandorascluster.dto.flag.FlagContainerDto;

import java.util.List;
import java.util.function.Function;

/**
 * Must be invoked while the Hibernate {@link org.hibernate.Session} that loaded the entity
 * is still open — this strategy traverses {@code land}, {@code naturalFlags},
 * {@code roleFlags} and {@code entityCapFlags} lazy associations.
 */
public final class FlagContainerMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return entity -> {
            if (entity == null) return null;
            if (!(entity instanceof FlagContainerDto flagContainer)) return null;

            MappingContext landCtx = MappingContext.create();
            landCtx.setMappingStrategy(LandMappingStrategy.create());
            landCtx.setMappingType(MapperType.ENTITY_TO_MODEL);
            Land land = (Land) landCtx.doMapping(flagContainer.land());

            MappingContext naturalCtx = MappingContext.create();
            naturalCtx.setMappingStrategy(NaturalFlagMappingStrategy.create());
            naturalCtx.setMappingType(MapperType.ENTITY_TO_MODEL);
            List<LandNaturalFlag> naturalFlags = flagContainer.naturalFlags()
                    .stream()
                    .map(flag -> (LandNaturalFlag) naturalCtx.doMapping(flag))
                    .toList();

            MappingContext roleCtx = MappingContext.create();
            roleCtx.setMappingStrategy(RoleFlagMappingStrategy.create());
            roleCtx.setMappingType(MapperType.ENTITY_TO_MODEL);
            List<LandRoleFlag> roleFlags = flagContainer.roleFlags()
                    .stream()
                    .map(flag -> (LandRoleFlag) roleCtx.doMapping(flag))
                    .toList();

            MappingContext capCtx = MappingContext.create();
            capCtx.setMappingStrategy(EntityCapFlagMappingStrategy.create());
            capCtx.setMappingType(MapperType.ENTITY_TO_MODEL);
            List<LandEntityCapFlag> entityCapFlags = flagContainer.entityCapFlags()
                    .stream()
                    .map(flag -> (LandEntityCapFlag) capCtx.doMapping(flag))
                    .toList();

            return new FlagContainer(flagContainer.id(), land, naturalFlags, roleFlags, entityCapFlags);
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {

            if (model == null) return null;
            if(!(model instanceof FlagContainer flagContainer)) return null;

            MappingContext landCtx = MappingContext.create();
            landCtx.setMappingStrategy(LandMappingStrategy.create());
            landCtx.setMappingType(MapperType.MODEL_TO_ENTITY);
            LandEntity land = (LandEntity) landCtx.doMapping(flagContainer.getLand());

            MappingContext naturalCtx = MappingContext.create();
            naturalCtx.setMappingStrategy(NaturalFlagMappingStrategy.create());
            naturalCtx.setMappingType(MapperType.MODEL_TO_ENTITY);
            List<LandNaturalFlagEntity> naturalFlags = flagContainer.getNaturalFlags()
                    .stream()
                    .map(flag -> (LandNaturalFlagEntity) naturalCtx.doMapping(flag))
                    .toList();

            MappingContext roleCtx = MappingContext.create();
            roleCtx.setMappingStrategy(RoleFlagMappingStrategy.create());
            roleCtx.setMappingType(MapperType.MODEL_TO_ENTITY);
            List<LandRoleFlagEntity> roleFlags = flagContainer.getRoleFlags()
                    .stream()
                    .map(flag -> (LandRoleFlagEntity) roleCtx.doMapping(flag))
                    .toList();

            MappingContext capCtx = MappingContext.create();
            capCtx.setMappingStrategy(EntityCapFlagMappingStrategy.create());
            capCtx.setMappingType(MapperType.MODEL_TO_ENTITY);
            List<LandEntityCapFlagEntity> entityCapFlags = flagContainer.getEntityCapFlags()
                    .stream()
                    .map(flag -> (LandEntityCapFlagEntity) capCtx.doMapping(flag))
                    .toList();

            return new FlagContainerEntity(flagContainer.getId(), land, naturalFlags, roleFlags, entityCapFlags);
        };
    }

    public static FlagContainerMappingStrategy create() {
        return new FlagContainerMappingStrategy();
    }
}
