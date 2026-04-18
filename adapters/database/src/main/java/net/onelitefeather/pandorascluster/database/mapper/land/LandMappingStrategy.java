package net.onelitefeather.pandorascluster.database.mapper.land;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.position.HomePosition;
import net.onelitefeather.pandorascluster.database.mapper.flag.FlagContainerMappingStrategy;
import net.onelitefeather.pandorascluster.database.mapper.player.LandPlayerMappingStrategy;
import net.onelitefeather.pandorascluster.database.mapper.position.HomePositionMappingStrategy;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;
import net.onelitefeather.pandorascluster.database.models.position.HomePositionEntity;

import java.util.List;
import java.util.function.Function;

/**
 * Must be invoked while the Hibernate {@link org.hibernate.Session} that loaded the entity
 * is still open — this strategy traverses {@code owner}, {@code home}, {@code flagContainer},
 * and {@code areas}. Use {@code DatabaseLandService#getLands()} as the canonical JOIN FETCH pattern.
 */
public final class LandMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return entity -> {
            if (entity == null) return null;
            if (!(entity instanceof LandEntity land)) return null;

            MappingContext areaCtx = MappingContext.create();
            areaCtx.setMappingStrategy(LandAreaMappingStrategy.create());
            areaCtx.setMappingType(MapperType.ENTITY_TO_MODEL);
            List<LandArea> landAreas = land.areas().stream()
                    .map(area -> (LandArea) areaCtx.doMapping(area))
                    .toList();

            MappingContext playerCtx = MappingContext.create();
            playerCtx.setMappingStrategy(LandPlayerMappingStrategy.create());
            playerCtx.setMappingType(MapperType.ENTITY_TO_MODEL);
            LandPlayer owner = (LandPlayer) playerCtx.doMapping(land.owner());

            MappingContext flagCtx = MappingContext.create();
            flagCtx.setMappingStrategy(FlagContainerMappingStrategy.create());
            flagCtx.setMappingType(MapperType.ENTITY_TO_MODEL);
            FlagContainer flagContainer = (FlagContainer) flagCtx.doMapping(land.flagContainer());

            MappingContext homeCtx = MappingContext.create();
            homeCtx.setMappingStrategy(HomePositionMappingStrategy.create());
            homeCtx.setMappingType(MapperType.ENTITY_TO_MODEL);
            HomePosition homePosition = (HomePosition) homeCtx.doMapping(land.home());

            return new Land(land.id(), owner, homePosition, landAreas, flagContainer);
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {
            if (model == null) return null;
            if (!(model instanceof Land land)) return null;

            MappingContext areaCtx = MappingContext.create();
            areaCtx.setMappingStrategy(LandAreaMappingStrategy.create());
            areaCtx.setMappingType(MapperType.MODEL_TO_ENTITY);
            List<LandAreaEntity> landAreas = land.getAreas().stream()
                    .map(area -> (LandAreaEntity) areaCtx.doMapping(area))
                    .toList();

            MappingContext playerCtx = MappingContext.create();
            playerCtx.setMappingStrategy(LandPlayerMappingStrategy.create());
            playerCtx.setMappingType(MapperType.MODEL_TO_ENTITY);
            LandPlayerEntity owner = (LandPlayerEntity) playerCtx.doMapping(land.getOwner());

            MappingContext flagCtx = MappingContext.create();
            flagCtx.setMappingStrategy(FlagContainerMappingStrategy.create());
            flagCtx.setMappingType(MapperType.MODEL_TO_ENTITY);
            FlagContainerEntity flagContainer = (FlagContainerEntity) flagCtx.doMapping(land.getFlagContainer());

            MappingContext homeCtx = MappingContext.create();
            homeCtx.setMappingStrategy(HomePositionMappingStrategy.create());
            homeCtx.setMappingType(MapperType.MODEL_TO_ENTITY);
            HomePositionEntity homePosition = (HomePositionEntity) homeCtx.doMapping(land.getHome());

            return new LandEntity(land.getId(), owner, homePosition, landAreas, flagContainer);
        };
    }

    public static LandMappingStrategy create() {
        return new LandMappingStrategy();
    }
}
