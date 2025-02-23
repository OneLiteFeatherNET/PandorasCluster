package net.onelitefeather.pandorascluster.database.mapper.impl;

import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper;
import net.onelitefeather.pandorascluster.database.models.flag.EntityCapFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.NaturalFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.RoleFlagEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.dbo.flag.EntityCapFlagDBO;
import net.onelitefeather.pandorascluster.dbo.flag.NaturalFlagDBO;
import net.onelitefeather.pandorascluster.dbo.flag.RoleFlagDBO;
import org.jetbrains.annotations.Nullable;

public final class FlagMapper {


    public static class RoleFlagMapper implements DatabaseEntityMapper<RoleFlagDBO, LandRoleFlag> {
        private final LandAreaMapper landAreaMapper;

        public RoleFlagMapper(LandAreaMapper landAreaMapper) {
            this.landAreaMapper = landAreaMapper;
        }

        @Override
        public @Nullable LandRoleFlag entityToModel(@Nullable RoleFlagDBO entity) {
            if (entity == null) return null;
            return new LandRoleFlag(
                    entity.id(),
                    entity.name(),
                    entity.state(),
                    entity.role(),
                    landAreaMapper.entityToModel(entity.landArea()));
        }

        @Override
        public @Nullable RoleFlagDBO modelToEntity(@Nullable LandRoleFlag model) {
            if (model == null) return null;
            return new RoleFlagEntity(
                    model.getId(),
                    model.getName(),
                    model.getState(),
                    model.getRole(),
                    (LandAreaEntity) landAreaMapper.modelToEntity(model.getLandArea()));
        }
    }

    public static class NaturalFlagMapper implements DatabaseEntityMapper<NaturalFlagDBO, LandNaturalFlag> {

        @Override
        public @Nullable LandNaturalFlag entityToModel(@Nullable NaturalFlagDBO entity) {
            if (entity == null) return null;
            return new LandNaturalFlag(entity.id(), entity.name(), entity.state());
        }

        @Override
        public @Nullable NaturalFlagDBO modelToEntity(@Nullable LandNaturalFlag model) {
            if (model == null) return null;
            return new NaturalFlagEntity(model.getId(), model.getName(), model.getState());
        }
    }

    public static class EntityCapFlagMapper implements DatabaseEntityMapper<EntityCapFlagDBO, LandEntityCapFlag> {

        @Override
        public @Nullable LandEntityCapFlag entityToModel(@Nullable EntityCapFlagDBO entity) {
            if (entity == null) return null;
            return new LandEntityCapFlag(entity.id(), entity.name(), entity.spawnLimit());
        }

        @Override
        public @Nullable EntityCapFlagDBO modelToEntity(@Nullable LandEntityCapFlag model) {
            if (model == null) return null;
            return new EntityCapFlagEntity(model.getId(), model.getName(), model.getSpawnLimit());
        }
    }
}
