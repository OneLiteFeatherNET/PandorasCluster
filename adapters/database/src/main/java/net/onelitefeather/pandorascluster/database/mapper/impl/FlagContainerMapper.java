package net.onelitefeather.pandorascluster.database.mapper.impl;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandEntityCapFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandNaturalFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandRoleFlagEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import net.onelitefeather.pandorascluster.dbo.flag.FlagContainerDBO;

import java.util.List;

public class FlagContainerMapper implements DatabaseEntityMapper<FlagContainerDBO, FlagContainer> {

    private final LandMapper landMapper;
    private final NaturalFlagMapper naturalFlagMapper;
    private final RoleFlagMapper roleFlagMapper;
    private final EntityCapFlagMapper entityCapFlagMapper;

    public FlagContainerMapper(LandMapper landMapper,
                               NaturalFlagMapper naturalFlagMapper,
                               RoleFlagMapper roleFlagMapper,
                               EntityCapFlagMapper entityCapFlagMapper) {
        this.landMapper = landMapper;
        this.naturalFlagMapper = naturalFlagMapper;
        this.roleFlagMapper = roleFlagMapper;
        this.entityCapFlagMapper = entityCapFlagMapper;
    }

    @Override
    public FlagContainer entityToModel(FlagContainerDBO entity) {
        if (entity == null) return null;
        List<LandEntityCapFlag> capFlags = entity.entityCapFlags().stream().map(entityCapFlagMapper::entityToModel).toList();
        List<LandRoleFlag> roleFlags = entity.roleFlags().stream().map(roleFlagMapper::entityToModel).toList();
        List<LandNaturalFlag> naturalFlags = entity.naturalFlags().stream().map(naturalFlagMapper::entityToModel).toList();
        Land land = this.landMapper.entityToModel(entity.land());
        return new FlagContainer(entity.id(), land, naturalFlags, roleFlags, capFlags);
    }

    @Override
    public FlagContainerDBO modelToEntity(FlagContainer model) {
        if (model == null) return null;

        List<LandEntityCapFlagEntity> capFlags = model.getEntityCapFlags()
                .stream()
                .map(flag -> (LandEntityCapFlagEntity) entityCapFlagMapper.modelToEntity(flag))
                .toList();

        List<LandRoleFlagEntity> roleFlags = model.getRoleFlags()
                .stream()
                .map(flag -> (LandRoleFlagEntity) roleFlagMapper.modelToEntity(flag))
                .toList();

        List<LandNaturalFlagEntity> naturalFlags = model.getNaturalFlags()
                .stream()
                .map(flag -> (LandNaturalFlagEntity) naturalFlagMapper.modelToEntity(flag))
                .toList();

        LandEntity land = (LandEntity) this.landMapper.modelToEntity(model.getLand());

        return new FlagContainerEntity(model.getId(), naturalFlags, roleFlags, capFlags, land);

    }
}
