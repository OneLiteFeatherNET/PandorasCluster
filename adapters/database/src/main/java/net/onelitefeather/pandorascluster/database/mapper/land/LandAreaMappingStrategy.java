package net.onelitefeather.pandorascluster.database.mapper.land;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.api.player.LandMember;
import net.onelitefeather.pandorascluster.database.mapper.player.LandMemberMappingStrategy;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class LandAreaMappingStrategy implements MapperStrategy {

    public static LandAreaMappingStrategy create() {
        return new LandAreaMappingStrategy();
    }

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return entity -> {

            if (entity == null) return null;
            if (!(entity instanceof LandAreaEntity landArea)) return null;

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(LandMappingStrategy.create());
            mappingContext.setMappingType(MapperType.ENTITY_TO_MODEL);

            List<ClaimedChunk> claimedChunks = landArea.chunks().stream().map(chunk -> (ClaimedChunk) mappingContext.doMapping(chunk)).toList();
            List<LandMember> members = landArea.members().stream().map(chunk -> (LandMember) mappingContext.doMapping(chunk)).toList();
            Land land = (Land) mappingContext.doMapping(landArea.land());

            return new LandArea(landArea.id(), landArea.name(), claimedChunks, members, land);
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {
            if (model == null) return null;
            if (!(model instanceof LandArea landArea)) return null;

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(LandMappingStrategy.create());
            mappingContext.setMappingType(MapperType.MODEL_TO_ENTITY);

            List<ClaimedChunkEntity> claimedChunks = landArea.getChunks().stream().map(chunk -> (ClaimedChunkEntity) mappingContext.doMapping(chunk)).toList();
            List<LandMemberEntity> members = landArea.getMembers().stream().map(chunk -> (LandMemberEntity) mappingContext.doMapping(chunk)).toList();
            LandEntity land = (LandEntity) mappingContext.doMapping(landArea.getLand());

            return new LandAreaEntity(landArea.getId(), landArea.getName(), members, claimedChunks, land);
        };
    }
}
