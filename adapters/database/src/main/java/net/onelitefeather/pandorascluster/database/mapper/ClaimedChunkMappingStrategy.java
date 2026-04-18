package net.onelitefeather.pandorascluster.database.mapper;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;

import java.util.function.Function;

/**
 * The chunk↔land-area back-reference is intentionally left null to avoid the
 * mapper cycle (LandArea → chunks → chunk.landArea → …). Callers that need the
 * parent area must look it up separately via {@code LandAreaService}.
 */
public final class ClaimedChunkMappingStrategy implements MapperStrategy {

    public static ClaimedChunkMappingStrategy create() {
        return new ClaimedChunkMappingStrategy();
    }

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return entity -> {
            if (!(entity instanceof ClaimedChunkEntity claimedChunkEntity)) return null;
            return new ClaimedChunk(claimedChunkEntity.id(), claimedChunkEntity.chunkIndex(), null);
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {
            if (model == null) return null;
            if (!(model instanceof ClaimedChunk claimedChunk)) return null;
            return new ClaimedChunkEntity(claimedChunk.getId(), claimedChunk.getChunkIndex(), null);
        };
    }
}
