package net.onelitefeather.pandorascluster.database.mapper.impl

import net.onelitefeather.pandorascluster.api.land.flag.FlagRoleAttachment
import net.onelitefeather.pandorascluster.database.mapper.DatabaseEntityMapper
import net.onelitefeather.pandorascluster.database.models.flag.FlagRoleAttachmentEntity


class FlagRoleAttachmentMapper : DatabaseEntityMapper<FlagRoleAttachmentEntity, FlagRoleAttachment> {

    override fun entityToModel(entity: FlagRoleAttachmentEntity?): FlagRoleAttachment? {
        if(entity == null) return null
        return FlagRoleAttachment(entity.id, entity.role, entity.value, entity.flag)
    }

    override fun modelToEntity(model: FlagRoleAttachment?): FlagRoleAttachmentEntity? {
        if(model == null) return null
        return FlagRoleAttachmentEntity(model.id, model.role, model.value, model.flag)
    }
}