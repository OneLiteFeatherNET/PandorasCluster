package net.onelitefeather.pandorascluster.database.mapper.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandEntityCapFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandNaturalFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandRoleFlagEntity;

import java.util.List;

/**
 * Must be invoked while the Hibernate session that loaded the entity is still open — this
 * mapper traverses {@code naturalFlags}, {@code roleFlags}, and {@code entityCapFlags} lazy
 * collections. The flag→container back-reference is intentionally left null on both sides.
 */
public final class FlagContainerMapper {

    private FlagContainerMapper() {
    }

    public static FlagContainer toModel(FlagContainerEntity entity) {
        if (entity == null) return null;
        return new FlagContainer(
                entity.id(),
                entity.naturalFlags().stream().map(NaturalFlagMapper::toModel).toList(),
                entity.roleFlags().stream().map(RoleFlagMapper::toModel).toList(),
                entity.entityCapFlags().stream().map(EntityCapFlagMapper::toModel).toList());
    }

    public static FlagContainerEntity toEntity(FlagContainer model) {
        if (model == null) return null;
        List<LandNaturalFlagEntity> naturalFlags = model.getNaturalFlags().stream().map(NaturalFlagMapper::toEntity).toList();
        List<LandRoleFlagEntity> roleFlags = model.getRoleFlags().stream().map(RoleFlagMapper::toEntity).toList();
        List<LandEntityCapFlagEntity> entityCapFlags = model.getEntityCapFlags().stream().map(EntityCapFlagMapper::toEntity).toList();
        return new FlagContainerEntity(model.getId(), null, naturalFlags, roleFlags, entityCapFlags);
    }
}
