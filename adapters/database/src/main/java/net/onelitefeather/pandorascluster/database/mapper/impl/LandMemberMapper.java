package net.onelitefeather.pandorascluster.database.mapper.impl;

import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper;
import net.onelitefeather.pandorascluster.api.player.LandMember;
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;
import net.onelitefeather.pandorascluster.dbo.player.LandMemberDBO;
import org.jetbrains.annotations.Nullable;

public final class LandMemberMapper implements DatabaseEntityMapper<LandMemberDBO, LandMember> {

    private final LandPlayerMapper landPlayerMapper;

    public LandMemberMapper(LandPlayerMapper landPlayerMapper) {
        this.landPlayerMapper = landPlayerMapper;
    }

    @Override
    public @Nullable LandMember entityToModel(@Nullable LandMemberDBO entity) {
        if (entity == null) return null;
        var landPlayer = landPlayerMapper.entityToModel(entity.member());
        if (landPlayer == null) return null;
        return new LandMember(entity.id(), landPlayer, entity.role());
    }

    @Override
    public @Nullable LandMemberDBO modelToEntity(@Nullable LandMember model) {
        if (model == null) return null;
        LandPlayerEntity landPlayer = (LandPlayerEntity) landPlayerMapper.modelToEntity(model.getMember());
        if (landPlayer == null) return null;
        return new LandMemberEntity(model.getId(), landPlayer, model.getRole(), null);
    }
}
