package net.onelitefeather.pandorascluster.database.mapper.impl;

import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.land.flag.AreaEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.AreaNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.AreaRoleFlag;
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.database.models.flag.EntityCapFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.NaturalFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.RoleFlagEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity;
import net.onelitefeather.pandorascluster.dbo.land.LandAreaDBO;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class LandAreaMapper implements DatabaseEntityMapper<LandAreaDBO, LandArea> {

    private final DatabaseService databaseService;

    private final LandMemberMapper memberMapper;
    private final FlagMapper.NaturalFlagMapper naturalFlagMapper;
    private final FlagMapper.RoleFlagMapper roleFlagMapper;
    private final FlagMapper.EntityCapFlagMapper entityCapFlagMapper;

    public LandAreaMapper(DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.memberMapper = new LandMemberMapper((LandPlayerMapper) databaseService.landPlayerMapper());
        this.naturalFlagMapper = new FlagMapper.NaturalFlagMapper();
        this.roleFlagMapper = new FlagMapper.RoleFlagMapper();
        this.entityCapFlagMapper = new FlagMapper.EntityCapFlagMapper();
    }


    @Override
    public @Nullable LandArea entityToModel(@Nullable LandAreaDBO entity) {
        if(entity == null) return null;

        List<AreaNaturalFlag> naturalFlags = entity.naturalFlags().stream().map(this.naturalFlagMapper::entityToModel).toList();
        List<AreaRoleFlag> roleFlags = entity.roleFlags().stream().map(this.roleFlagMapper::entityToModel).toList();
        List<AreaEntityCapFlag> entityCapFlags = entity.entityCapFlags().stream().map(this.entityCapFlagMapper::entityToModel).toList();

        var chunks = entity.chunks().stream().map(this.databaseService.chunkMapper()::entityToModel).toList();
        var members = entity.members().stream().map(this.memberMapper::entityToModel).toList();
        var land = this.databaseService.landMapper().entityToModel(entity.land());

        return new LandArea(entity.id(), entity.name(), naturalFlags, roleFlags, entityCapFlags, chunks, members, land);
    }

    @Override
    public @Nullable LandAreaDBO modelToEntity(@Nullable LandArea model) {
        if(model == null) return null;

        List<NaturalFlagEntity> naturalFlags = model.getNaturalFlags()
                .stream().map(flag -> (NaturalFlagEntity) this.naturalFlagMapper.modelToEntity(flag))
                .toList();

        List<RoleFlagEntity> roleFlags = model.getRoleFlags()
                .stream().map(flag -> (RoleFlagEntity) this.roleFlagMapper.modelToEntity(flag))
                .toList();

        List<EntityCapFlagEntity> entityCapFlags = model.getEntityCapFlags()
                .stream().map(flag -> (EntityCapFlagEntity) this.entityCapFlagMapper.modelToEntity(flag))
                .toList();

        List<ClaimedChunkEntity> chunks = model.getChunks()
                .stream().map(chunk -> (ClaimedChunkEntity) this.databaseService.chunkMapper().modelToEntity(chunk))
                .toList();

        List<LandMemberEntity> members = model.getMembers()
                .stream().map(landMember -> (LandMemberEntity) this.memberMapper.modelToEntity(landMember))
                .toList();

        LandEntity land = (LandEntity) this.databaseService.landMapper().modelToEntity(model.getLand());

        return new LandAreaEntity(model.getId(), model.getName(), members, chunks, roleFlags, naturalFlags, entityCapFlags, land);
    }


    public FlagMapper.EntityCapFlagMapper getEntityCapFlagMapper() {
        return entityCapFlagMapper;
    }

    public LandMemberMapper getMemberMapper() {
        return memberMapper;
    }

    public FlagMapper.NaturalFlagMapper getNaturalFlagMapper() {
        return naturalFlagMapper;
    }

    public FlagMapper.RoleFlagMapper getRoleFlagMapper() {
        return roleFlagMapper;
    }
}
