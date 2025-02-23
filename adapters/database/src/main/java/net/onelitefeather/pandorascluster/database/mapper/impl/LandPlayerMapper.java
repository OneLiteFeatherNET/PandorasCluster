package net.onelitefeather.pandorascluster.database.mapper.impl;

import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;
import net.onelitefeather.pandorascluster.dbo.player.LandPlayerDBO;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class LandPlayerMapper implements DatabaseEntityMapper<LandPlayerDBO, LandPlayer> {

    @Override
    public @Nullable LandPlayer entityToModel(@Nullable LandPlayerDBO entity) {
        if (entity == null) return null;
        return new LandPlayer(entity.id(), UUID.fromString(entity.uuid()), entity.name());
    }

    @Override
    public @Nullable LandPlayerDBO modelToEntity(@Nullable LandPlayer model) {
        if(model == null) return null;
        return new LandPlayerEntity(model.getId(), model.getUniqueId().toString(), model.getName());
    }
}
