package net.onelitefeather.pandorascluster.database.mapper.impl;

import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper;
import net.onelitefeather.pandorascluster.api.position.HomePosition;
import net.onelitefeather.pandorascluster.database.models.position.HomePositionEntity;
import net.onelitefeather.pandorascluster.dbo.position.HomePositionDBO;
import org.jetbrains.annotations.Nullable;

public final class HomePositionMapper implements DatabaseEntityMapper<HomePositionDBO, HomePosition> {

    @Override
    public @Nullable HomePosition entityToModel(@Nullable HomePositionDBO entity) {
        if(entity == null) return null;
        return new HomePosition(entity.id(), entity.posX(), entity.posY(), entity.posZ(), entity.yaw(), entity.pitch());
    }

    @Override
    public @Nullable HomePositionDBO modelToEntity(@Nullable HomePosition model) {
        if(model == null) return null;
        return new HomePositionEntity(model.getId(), model.getPosX(), model.getPosY(), model.getPosZ(), model.getYaw(), model.getPitch());
    }
}
