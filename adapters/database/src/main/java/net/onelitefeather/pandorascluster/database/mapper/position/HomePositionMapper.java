package net.onelitefeather.pandorascluster.database.mapper.position;

import net.onelitefeather.pandorascluster.api.position.HomePosition;
import net.onelitefeather.pandorascluster.database.models.position.HomePositionEntity;

public final class HomePositionMapper {

    private HomePositionMapper() {
    }

    public static HomePosition toModel(HomePositionEntity entity) {
        if (entity == null) return null;
        return new HomePosition(
                entity.id(),
                entity.world(),
                entity.posX(),
                entity.posY(),
                entity.posZ(),
                entity.yaw(),
                entity.pitch());
    }

    public static HomePositionEntity toEntity(HomePosition model) {
        if (model == null) return null;
        return new HomePositionEntity(
                model.getId(),
                model.getWorld(),
                model.getPosX(),
                model.getPosY(),
                model.getPosZ(),
                model.getYaw(),
                model.getPitch());
    }
}
