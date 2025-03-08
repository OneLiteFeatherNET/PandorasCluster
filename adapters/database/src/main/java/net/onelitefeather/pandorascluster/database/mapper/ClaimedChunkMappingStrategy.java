package net.onelitefeather.pandorascluster.database.mapper;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.database.mapper.land.LandAreaMappingStrategy;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;

import java.util.function.Function;

public final class ClaimedChunkMappingStrategy implements MapperStrategy {

    public static ClaimedChunkMappingStrategy create() {
        return new ClaimedChunkMappingStrategy();
    }

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return entity -> {
            if (!(entity instanceof ClaimedChunkEntity claimedChunkEntity)) return null;

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(LandAreaMappingStrategy.create());
            mappingContext.setMappingType(MapperType.ENTITY_TO_MODEL);

            LandArea landArea = (LandArea) mappingContext.doMapping(claimedChunkEntity.landArea());
            return new ClaimedChunk(claimedChunkEntity.id(), claimedChunkEntity.chunkIndex(), landArea);
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {

            if (model == null) return null;
            if (!(model instanceof ClaimedChunk claimedChunk)) return null;

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(LandAreaMappingStrategy.create());
            mappingContext.setMappingType(MapperType.MODEL_TO_ENTITY);

            LandAreaEntity landArea = (LandAreaEntity) mappingContext.doMapping(claimedChunk.getLandArea());

            return new ClaimedChunkEntity(claimedChunk.getId(), claimedChunk.getChunkIndex(), landArea);
        };
    }
}
