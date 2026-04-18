package net.onelitefeather.pandorascluster.database.mapper.land;

import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.database.mapper.ClaimedChunkMapper;
import net.onelitefeather.pandorascluster.database.mapper.player.LandMemberMapper;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity;

import java.util.List;

/**
 * Must be invoked while the Hibernate session that loaded the entity is still open — this
 * mapper traverses the {@code members} and {@code chunks} lazy collections.
 */
public final class LandAreaMapper {

    private LandAreaMapper() {
    }

    public static LandArea toModel(LandAreaEntity entity) {
        if (entity == null) return null;
        Long landId = entity.land() != null ? entity.land().id() : null;
        return new LandArea(
                entity.id(),
                landId,
                entity.name(),
                entity.chunks().stream().map(ClaimedChunkMapper::toModel).toList(),
                entity.members().stream().map(LandMemberMapper::toModel).toList());
    }

    public static LandAreaEntity toEntity(LandArea model) {
        if (model == null) return null;
        List<ClaimedChunkEntity> chunks = model.getChunks().stream().map(ClaimedChunkMapper::toEntity).toList();
        List<LandMemberEntity> members = model.getMembers().stream().map(LandMemberMapper::toEntity).toList();
        LandEntity landRef = null; // parent link is set by the service layer when persisting
        return new LandAreaEntity(model.getId(), model.getName(), members, chunks, landRef);
    }
}
