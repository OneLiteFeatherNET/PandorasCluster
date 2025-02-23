package net.onelitefeather.pandorascluster.database.mapper.impl

import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper
import net.onelitefeather.pandorascluster.api.player.LandMember
import net.onelitefeather.pandorascluster.api.service.DatabaseService
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity
import net.onelitefeather.pandorascluster.dbo.player.LandMemberDBO

class LandMemberMapper(private val databaseService: DatabaseService) :
    DatabaseEntityMapper<LandMemberDBO, LandMember> {

    override fun entityToModel(entity: LandMemberDBO?): LandMember? {
        if (entity == null) return null

        val member = databaseService.landPlayerMapper().entityToModel(entity.member()) ?: return null
        return LandMember(entity.id(), member, entity.role())
    }

    override fun modelToEntity(model: LandMember?): LandMemberDBO? {
        if (model == null) return null
        val member = databaseService.landPlayerMapper().modelToEntity(model.member) ?: return null
        return LandMemberEntity(model.id, member as LandPlayerEntity, model.role)
    }
}