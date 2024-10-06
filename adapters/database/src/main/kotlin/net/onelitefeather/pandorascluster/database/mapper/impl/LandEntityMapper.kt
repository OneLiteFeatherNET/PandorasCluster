package net.onelitefeather.pandorascluster.database.mapper.impl

import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper
import net.onelitefeather.pandorascluster.api.service.DatabaseService
import net.onelitefeather.pandorascluster.database.models.ClaimedChunkEntity
import net.onelitefeather.pandorascluster.database.models.LandEntity
import net.onelitefeather.pandorascluster.database.models.flag.FlagRoleAttachmentEntity
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity
import net.onelitefeather.pandorascluster.database.models.position.HomePositionEntity
import net.onelitefeather.pandorascluster.dbo.land.LandDBO

class LandEntityMapper(private val databaseService: DatabaseService) : DatabaseEntityMapper<LandDBO, Land> {

    override fun entityToModel(entity: LandDBO?): Land? {
        if (entity == null) return null

        val members = entity.members().mapNotNull { databaseService.landMemberMapper().entityToModel(it) }
        val chunks = entity.chunks().mapNotNull { databaseService.claimedChunkMapper().entityToModel(it) }
        val flags = entity.flags().mapNotNull { databaseService.flagMapper().entityToModel(it) }

        return Land(
            entity.id(),
            databaseService.landPlayerMapper().entityToModel(entity.owner()),
            databaseService.homePositionMapper().entityToModel(entity.home()),
            members,
            chunks,
            flags,
            entity.world()
        )
    }

    override fun modelToEntity(model: Land?): LandDBO? {
        if (model == null) return null

        val members = model.members.map { databaseService.landMemberMapper().modelToEntity(it) as LandMemberEntity }
        val chunks = model.chunks.map { databaseService.claimedChunkMapper().modelToEntity(it) as ClaimedChunkEntity }
        val flags = model.flags.map { databaseService.flagMapper().modelToEntity(it) as FlagRoleAttachmentEntity }

        return LandEntity(
            model.id,
            databaseService.landPlayerMapper().modelToEntity(model.owner) as LandPlayerEntity,
            databaseService.homePositionMapper().modelToEntity(model.home) as HomePositionEntity,
            members,
            chunks,
            flags,
            model.world
        )
    }
}