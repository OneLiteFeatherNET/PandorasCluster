package net.onelitefeather.pandorascluster.database.mapper;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;

import java.util.function.Function;

public final class ClaimedChunkMappingStrategy implements MapperStrategy {

    public static MapperStrategy create() {
        return new ClaimedChunkMappingStrategy();
    }

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return databaseEntity -> {
            if (databaseEntity instanceof ClaimedChunkEntity claimedChunkEntity) {
                return new ClaimedChunk(claimedChunkEntity.id(), claimedChunkEntity.chunkIndex());
            }
            return null;
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return pandorasModel -> {
            if (pandorasModel instanceof ClaimedChunk claimedChunk) {
                return new ClaimedChunkEntity(claimedChunk.getId(), claimedChunk.getChunkIndex(), null);
            }
            return null;
        };
    }
}
