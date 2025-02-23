package net.onelitefeather.pandorascluster.database.mapper.impl;

import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity;
import net.onelitefeather.pandorascluster.dbo.land.LandAreaDBO;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class LandAreaMapper implements DatabaseEntityMapper<LandAreaDBO, LandArea> {

    private final DatabaseService databaseService;

    private final LandMemberMapper memberMapper;

    public LandAreaMapper(DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.memberMapper = new LandMemberMapper((LandPlayerMapper) databaseService.landPlayerMapper());
    }

    @Override
    public @Nullable LandArea entityToModel(@Nullable LandAreaDBO entity) {
        if(entity == null) return null;

        var chunks = entity.chunks().stream().map(this.databaseService.chunkMapper()::entityToModel).toList();
        var members = entity.members().stream().map(this.memberMapper::entityToModel).toList();
        var land = this.databaseService.landMapper().entityToModel(entity.land());

        return new LandArea(entity.id(), entity.name(), chunks, members, land);
    }

    @Override
    public @Nullable LandAreaDBO modelToEntity(@Nullable LandArea model) {
        if(model == null) return null;

        List<ClaimedChunkEntity> chunks = model.getChunks()
                .stream().map(chunk -> (ClaimedChunkEntity) this.databaseService.chunkMapper().modelToEntity(chunk))
                .toList();

        List<LandMemberEntity> members = model.getMembers()
                .stream().map(landMember -> (LandMemberEntity) this.memberMapper.modelToEntity(landMember))
                .toList();

        LandEntity land = (LandEntity) this.databaseService.landMapper().modelToEntity(model.getLand());

        return new LandAreaEntity(model.getId(), model.getName(), members, chunks, land);
    }

    public LandMemberMapper getMemberMapper() {
        return memberMapper;
    }

}
