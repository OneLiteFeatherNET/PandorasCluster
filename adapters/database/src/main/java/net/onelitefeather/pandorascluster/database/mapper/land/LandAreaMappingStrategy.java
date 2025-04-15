package net.onelitefeather.pandorascluster.database.mapper.land;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.api.player.LandMember;
import net.onelitefeather.pandorascluster.database.mapper.ClaimedChunkMappingStrategy;
import net.onelitefeather.pandorascluster.database.mapper.player.LandMemberMappingStrategy;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity;
import net.onelitefeather.pandorascluster.dbo.chunk.ClaimedChunkDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandDBO;
import net.onelitefeather.pandorascluster.dbo.player.LandMemberDBO;

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
            return new LandArea(landArea.id(), landArea.name(), getChunks(landArea.chunks()), getMembers(landArea.members()), null);
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {
            if (model == null) return null;
            if (!(model instanceof LandArea landArea)) return null;

            Land land = landArea.getLand();
            if (land == null) return null;
            return new LandAreaEntity(
                    landArea.getId(),
                    landArea.getName(),
                    getMemberEntities(landArea.getMembers()),
                    getChunkEntities(landArea.getChunks()),
                    null);
        };
    }

    private Land getLand(LandDBO land) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(LandMappingStrategy.create());
        mappingContext.setMappingType(MapperType.ENTITY_TO_MODEL);
        return (Land) mappingContext.doMapping(land);
    }

    private List<ClaimedChunk> getChunks(List<ClaimedChunkDBO> chunks) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(create());
        mappingContext.setMappingType(MapperType.ENTITY_TO_MODEL);
        return chunks.stream().map(chunk -> (ClaimedChunk) mappingContext.doMapping(chunk)).toList();
    }

    private List<LandMember> getMembers(List<LandMemberDBO> members) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(LandMemberMappingStrategy.create());
        mappingContext.setMappingType(MapperType.ENTITY_TO_MODEL);
        return members.stream().map(chunk -> (LandMember) mappingContext.doMapping(chunk)).toList();
    }

    private LandEntity getLandEntity(Land land) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(LandMappingStrategy.create());
        mappingContext.setMappingType(MapperType.MODEL_TO_ENTITY);
        return (LandEntity) mappingContext.doMapping(land);
    }

    private List<LandMemberEntity> getMemberEntities(List<LandMember> members) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(LandMemberMappingStrategy.create());
        mappingContext.setMappingType(MapperType.MODEL_TO_ENTITY);
        return members.stream().map(chunk -> (LandMemberEntity) mappingContext.doMapping(chunk)).toList();
    }

    private List<ClaimedChunkEntity> getChunkEntities(List<ClaimedChunk> chunks) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(ClaimedChunkMappingStrategy.create());
        mappingContext.setMappingType(MapperType.MODEL_TO_ENTITY);
        return chunks.stream().map(chunk -> (ClaimedChunkEntity) mappingContext.doMapping(chunk)).toList();
    }
}
