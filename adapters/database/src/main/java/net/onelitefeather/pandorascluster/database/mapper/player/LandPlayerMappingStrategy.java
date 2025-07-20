package net.onelitefeather.pandorascluster.database.mapper.player;

import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;

import java.util.UUID;
import java.util.function.Function;

public final class LandPlayerMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return databaseEntity -> {
            if (databaseEntity instanceof LandPlayerEntity landPlayerEntity) {
                return new LandPlayer(landPlayerEntity.id(), UUID.fromString(landPlayerEntity.uuid()), landPlayerEntity.name());
            }
            return null;
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return pandorasModel -> {
            if (pandorasModel instanceof LandPlayer landPlayer) {
                return new LandPlayerEntity(landPlayer.getId(), landPlayer.getUniqueId().toString(), landPlayer.getName());
            }
            return null;
        };
    }

    public static LandPlayerMappingStrategy create() {
        return new LandPlayerMappingStrategy();
    }
}
