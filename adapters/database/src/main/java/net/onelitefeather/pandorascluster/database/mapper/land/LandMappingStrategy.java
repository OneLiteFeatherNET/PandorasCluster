package net.onelitefeather.pandorascluster.database.mapper.land;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.position.HomePosition;
import net.onelitefeather.pandorascluster.database.mapper.ClaimedChunkMappingStrategy;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;
import net.onelitefeather.pandorascluster.database.models.position.HomePositionEntity;
import net.onelitefeather.pandorascluster.dbo.land.LandDBO;

import java.util.List;
import java.util.function.Function;

public class LandMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return model -> {
            if(model == null) return null;
            if(!(model instanceof LandDBO land)) return null;

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(ClaimedChunkMappingStrategy.create());
            mappingContext.setMappingType(MapperStrategy.MapperType.ENTITY_TO_MODEL);

            List<LandArea> landAreas = land.areas().stream().map(area -> (LandArea) mappingContext.doMapping(area)).toList();

            LandPlayer owner = (LandPlayer) mappingContext.doMapping(land.owner());
            FlagContainer flagContainer = (FlagContainer) mappingContext.doMapping(land.flagContainer());
            HomePosition homePosition = (HomePosition) mappingContext.doMapping(land.home());
            return new Land(land.id(), owner, homePosition, landAreas, flagContainer);
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return entity -> {
            if(entity == null) return null;
            if(!(entity instanceof Land land)) return null;

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(ClaimedChunkMappingStrategy.create());
            mappingContext.setMappingType(MapperType.MODEL_TO_ENTITY);

            List<LandAreaEntity> landAreas = land.getAreas().stream().map(area -> (LandAreaEntity) mappingContext.doMapping(area)).toList();

            LandPlayerEntity owner = (LandPlayerEntity) mappingContext.doMapping(land.getOwner());
            HomePositionEntity homePosition = (HomePositionEntity) mappingContext.doMapping(land.getHome());
            FlagContainerEntity flagContainer = (FlagContainerEntity) mappingContext.doMapping(land.getFlagContainer());

            return new LandEntity(land.getId(), owner, homePosition, landAreas, flagContainer);
        };
    }

    public static LandMappingStrategy create() {
        return new LandMappingStrategy();
    }
}
