package net.onelitefeather.pandorascluster.database.mapper.impl

import net.onelitefeather.pandorascluster.api.player.LandPlayer
import net.onelitefeather.pandorascluster.database.mapper.DatabaseEntityMapper
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity
import java.util.*

class LandPlayerMapper : DatabaseEntityMapper<LandPlayerEntity, LandPlayer> {

    override fun entityToModel(entity: LandPlayerEntity?): LandPlayer? {
        if(entity == null) return null
        return LandPlayer(entity.id, UUID.fromString(entity.uuid), entity.name ?: "")
    }

    override fun modelToEntity(model: LandPlayer?): LandPlayerEntity? {
        if(model == null) return null
        return LandPlayerEntity(model.id, model.uniqueId.toString(), model.name)
    }
}