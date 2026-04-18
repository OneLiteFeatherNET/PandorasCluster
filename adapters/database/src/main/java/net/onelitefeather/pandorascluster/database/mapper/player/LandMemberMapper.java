package net.onelitefeather.pandorascluster.database.mapper.player;

import net.onelitefeather.pandorascluster.api.player.LandMember;
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity;

public final class LandMemberMapper {

    private LandMemberMapper() {
    }

    public static LandMember toModel(LandMemberEntity entity) {
        if (entity == null) return null;
        return new LandMember(entity.id(), LandPlayerMapper.toModel(entity.member()), entity.role());
    }

    public static LandMemberEntity toEntity(LandMember model) {
        if (model == null) return null;
        return new LandMemberEntity(model.getId(), LandPlayerMapper.toEntity(model.getMember()), model.getRole(), null);
    }
}
