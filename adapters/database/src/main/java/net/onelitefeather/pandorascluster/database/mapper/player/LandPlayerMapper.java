package net.onelitefeather.pandorascluster.database.mapper.player;

import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;

import java.util.UUID;

public final class LandPlayerMapper {

    private LandPlayerMapper() {
    }

    public static LandPlayer toModel(LandPlayerEntity entity) {
        if (entity == null) return null;
        return new LandPlayer(entity.id(), UUID.fromString(entity.uuid()), entity.name());
    }

    public static LandPlayerEntity toEntity(LandPlayer model) {
        if (model == null) return null;
        return new LandPlayerEntity(model.getId(), model.getUniqueId().toString(), model.getName());
    }
}
