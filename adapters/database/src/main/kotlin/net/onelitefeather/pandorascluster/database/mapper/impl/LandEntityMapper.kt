package net.onelitefeather.pandorascluster.database.mapper.impl

import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.database.mapper.DatabaseEntityMapper
import net.onelitefeather.pandorascluster.database.models.LandEntity
import net.onelitefeather.pandorascluster.database.service.DatabaseServiceImpl

class LandEntityMapper(private val databaseService: DatabaseServiceImpl) : DatabaseEntityMapper<LandEntity, Land> {

    override fun entityToModel(entity: LandEntity?): Land? {
        if (entity == null) return null

        val members = entity.members.mapNotNull { databaseService.landMemberMapper().entityToModel(it) }
        val chunks = entity.chunks.mapNotNull { databaseService.claimedChunkMapper().entityToModel(it) }
        val flags = entity.flags.mapNotNull { databaseService.flagMapper().entityToModel(it) }

        return Land(
            entity.id,
            databaseService.landPlayerMapper().entityToModel(entity.owner),
            databaseService.homePositionMapper().entityToModel(entity.home),
            members,
            chunks,
            flags,
            entity.world
        )
    }

    override fun modelToEntity(model: Land?): LandEntity? {
        if (model == null) return null

        val members = model.members.mapNotNull { databaseService.landMemberMapper().modelToEntity(it) }
        val chunks = model.chunks.mapNotNull { databaseService.claimedChunkMapper().modelToEntity(it) }
        val flags = model.flags.mapNotNull { databaseService.flagMapper().modelToEntity(it) }

        return LandEntity(
            model.id,
            databaseService.landPlayerMapper().modelToEntity(model.owner),
            databaseService.homePositionMapper().modelToEntity(model.home),
            members,
            chunks,
            flags,
            model.world
        )
    }
}