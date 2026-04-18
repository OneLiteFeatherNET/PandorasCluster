package net.onelitefeather.pandorascluster.database.mapper.flag;

import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import net.onelitefeather.pandorascluster.database.models.flag.LandRoleFlagEntity;

public final class RoleFlagMapper {

    private RoleFlagMapper() {
    }

    public static LandRoleFlag toModel(LandRoleFlagEntity entity) {
        if (entity == null) return null;
        return new LandRoleFlag(entity.id(), entity.name(), entity.state(), entity.role(), null);
    }

    public static LandRoleFlagEntity toEntity(LandRoleFlag model) {
        if (model == null) return null;
        return new LandRoleFlagEntity(model.getId(), model.getName(), model.getState(), model.getRole(), null);
    }
}
