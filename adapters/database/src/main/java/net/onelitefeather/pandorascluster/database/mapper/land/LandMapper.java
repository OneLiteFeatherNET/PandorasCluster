package net.onelitefeather.pandorascluster.database.mapper.land;

import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.database.mapper.flag.FlagContainerMapper;
import net.onelitefeather.pandorascluster.database.mapper.player.LandPlayerMapper;
import net.onelitefeather.pandorascluster.database.mapper.position.HomePositionMapper;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;

/**
 * Must be invoked while the Hibernate session that loaded the entity is still open —
 * traverses {@code owner}, {@code home}, {@code flagContainer}, and {@code areas}.
 */
public final class LandMapper {

    private LandMapper() {
    }

    public static Land toModel(LandEntity entity) {
        if (entity == null) return null;
        return new Land(
                entity.id(),
                LandPlayerMapper.toModel(entity.owner()),
                HomePositionMapper.toModel(entity.home()),
                entity.areas().stream().map(LandAreaMapper::toModel).toList(),
                FlagContainerMapper.toModel(entity.flagContainer()));
    }

    public static LandEntity toEntity(Land model) {
        if (model == null) return null;
        return new LandEntity(
                model.getId(),
                LandPlayerMapper.toEntity(model.getOwner()),
                HomePositionMapper.toEntity(model.getHome()),
                model.getAreas().stream().map(LandAreaMapper::toEntity).toList(),
                FlagContainerMapper.toEntity(model.getFlagContainer()));
    }
}
