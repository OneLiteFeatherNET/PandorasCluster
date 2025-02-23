package net.onelitefeather.pandorascluster.database.mapper.impl;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.dbo.chunk.ClaimedChunkDBO;
import org.jetbrains.annotations.Nullable;

public final class ClaimedChunkMapper implements DatabaseEntityMapper<ClaimedChunkDBO, ClaimedChunk> {

    @Override
    public ClaimedChunk entityToModel(@Nullable ClaimedChunkDBO entity) {
        if (entity == null) return null;
        return new ClaimedChunk(entity.id(), entity.chunkIndex());
    }

    @Override
    public ClaimedChunkDBO modelToEntity(@Nullable ClaimedChunk model) {
        if (model == null) return null;
        return new ClaimedChunkEntity(model.getId(), model.getChunkIndex());
    }
}
