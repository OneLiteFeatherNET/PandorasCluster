package net.onelitefeather.pandorascluster.database.mapper.land;

import net.onelitefeather.pandorascluster.api.land.LandWorld;
import net.onelitefeather.pandorascluster.database.mapper.flag.NaturalFlagMapper;
import net.onelitefeather.pandorascluster.database.models.land.LandWorldEntity;

import java.util.Collections;
import java.util.UUID;

public class LandWorldMapper {

    public static LandWorld toModel(LandWorldEntity entity) {
        if (entity == null) return null;
        return new LandWorld(
                entity.id(),
                UUID.fromString(entity.uuid()),
                entity.name(),
                entity.naturalFlags().stream().map(NaturalFlagMapper::toModel).toList(),
                Collections.emptyList());
    }

    public static LandWorldEntity toEntity(LandWorld model) {
        if (model == null) return null;
        return new LandWorldEntity(
                model.id(),
                model.worldId().toString(),
                model.name(),
                model.naturalFlags().stream().map(NaturalFlagMapper::toEntity).toList(),
                null);
    }
}
