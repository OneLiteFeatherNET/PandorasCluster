package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LandAreaService {

    /**
     * @param chunk the chunk to claim
     * @param landArea the land area to add the chunk
     */
    void claimChunk(@NotNull ClaimedChunk chunk, @Nullable LandArea landArea);

    /**
     * @param chunkIndex the chunk to remove from the land.
     **/
    boolean removeClaimedChunk(long chunkIndex);


    default boolean isChunkClaimed(@NotNull ClaimedChunk chunk) {
        return isChunkClaimed(chunk.getChunkIndex());
    }

    default boolean isChunkClaimed(long chunkIndex) {
        return getClaimedChunk(chunkIndex) != null;
    }

    @Nullable
    ClaimedChunk getClaimedChunk(long chunkIndex);

    @Nullable
    LandArea getLandArea(long chunkIndex);

    @Nullable
    default LandArea getLandArea(@NotNull ClaimedChunk chunk) {
        return getLandArea(chunk.getChunkIndex());
    }

    void unclaimArea(LandArea landArea);
}
