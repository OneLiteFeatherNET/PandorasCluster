package net.onelitefeather.pandorascluster.database.mapper.impl

import net.onelitefeather.pandorascluster.api.land.flag.FlagRoleAttachment
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper
import net.onelitefeather.pandorascluster.database.models.flag.FlagRoleAttachmentEntity
import net.onelitefeather.pandorascluster.dbo.flag.FlagRoleAttachmentDBO


class FlagRoleAttachmentMapper :
    DatabaseEntityMapper<FlagRoleAttachmentDBO, FlagRoleAttachment> {

    override fun entityToModel(entity: FlagRoleAttachmentDBO?): FlagRoleAttachment? {
        if (entity == null) return null
        return FlagRoleAttachment(entity.id(), entity.role(), entity.flag())
    }

    override fun modelToEntity(model: FlagRoleAttachment?): FlagRoleAttachmentDBO? {
        if (model == null) return null
        return FlagRoleAttachmentEntity(model.id, model.role, model.flag)
    }
}