package net.onelitefeather.pandorascluster.database.mapper.impl

import net.onelitefeather.pandorascluster.api.position.HomePosition
import net.onelitefeather.pandorascluster.database.mapper.DatabaseEntityMapper
import net.onelitefeather.pandorascluster.database.models.position.HomePositionEntity

class HomePositionMapper : DatabaseEntityMapper<HomePositionEntity, HomePosition> {

    override fun entityToModel(entity: HomePositionEntity?): HomePosition? {
        if (entity == null) return null
        return HomePosition(entity.id, entity.posX, entity.posY, entity.posZ, entity.yaw, entity.pitch)
    }

    override fun modelToEntity(model: HomePosition?): HomePositionEntity? {
        if(model == null) return null
        return HomePositionEntity(model.id, model.posX, model.posY, model.posZ, model.yaw, model.pitch)
    }
}