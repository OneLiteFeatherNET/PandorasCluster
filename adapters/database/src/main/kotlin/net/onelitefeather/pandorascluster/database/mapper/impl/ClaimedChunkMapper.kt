package net.onelitefeather.pandorascluster.database.mapper.impl

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk
import net.onelitefeather.pandorascluster.database.mapper.DatabaseEntityMapper
import net.onelitefeather.pandorascluster.database.models.ClaimedChunkEntity

class ClaimedChunkMapper : DatabaseEntityMapper<ClaimedChunkEntity, ClaimedChunk> {
    override fun entityToModel(entity: ClaimedChunkEntity?): ClaimedChunk? {
        if(entity == null) return null
        return ClaimedChunk(entity.id, entity.chunkIndex)
    }

    override fun modelToEntity(model: ClaimedChunk?): ClaimedChunkEntity? {
        if(model == null) return null
        return ClaimedChunkEntity(model.id, model.chunkIndex)
    }
}