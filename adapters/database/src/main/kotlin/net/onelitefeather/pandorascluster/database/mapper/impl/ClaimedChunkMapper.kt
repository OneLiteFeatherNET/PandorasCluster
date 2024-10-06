package net.onelitefeather.pandorascluster.database.mapper.impl

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper
import net.onelitefeather.pandorascluster.database.models.ClaimedChunkEntity
import net.onelitefeather.pandorascluster.dbo.chunk.ClaimedChunkDBO

class ClaimedChunkMapper : DatabaseEntityMapper<ClaimedChunkDBO, ClaimedChunk> {
    override fun entityToModel(entity: ClaimedChunkDBO?): ClaimedChunk? {
        if (entity == null) return null
        return ClaimedChunk(entity.id(), entity.chunkIndex())
    }

    override fun modelToEntity(model: ClaimedChunk?): ClaimedChunkDBO? {
        if (model == null) return null
        return ClaimedChunkEntity(model.id, model.chunkIndex)
    }
}