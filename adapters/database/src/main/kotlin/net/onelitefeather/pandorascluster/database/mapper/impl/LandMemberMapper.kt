package net.onelitefeather.pandorascluster.database.mapper.impl

import net.onelitefeather.pandorascluster.api.player.LandMember
import net.onelitefeather.pandorascluster.database.mapper.DatabaseEntityMapper
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity
import net.onelitefeather.pandorascluster.database.service.DatabaseServiceImpl

class LandMemberMapper(private val databaseService: DatabaseServiceImpl) :
    DatabaseEntityMapper<LandMemberEntity, LandMember> {

    override fun entityToModel(entity: LandMemberEntity?): LandMember? {
        if (entity == null) return null

        val member = databaseService.landPlayerMapper().entityToModel(entity.member) ?: return null
        return LandMember(entity.id, member, entity.role)
    }

    override fun modelToEntity(model: LandMember?): LandMemberEntity? {
        if (model == null) return null
        val member = databaseService.landPlayerMapper().modelToEntity(model.member) ?: return null
        return LandMemberEntity(model.id, member, model.role)
    }
}