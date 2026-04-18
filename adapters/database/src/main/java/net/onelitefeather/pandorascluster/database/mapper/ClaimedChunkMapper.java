package net.onelitefeather.pandorascluster.database.mapper;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;

public final class ClaimedChunkMapper {

    private ClaimedChunkMapper() {
    }

    public static ClaimedChunk toModel(ClaimedChunkEntity entity) {
        if (entity == null) return null;
        return new ClaimedChunk(entity.id(), entity.chunkIndex());
    }

    public static ClaimedChunkEntity toEntity(ClaimedChunk model) {
        if (model == null) return null;
        return new ClaimedChunkEntity(model.getId(), model.getChunkIndex(), null);
    }
}
