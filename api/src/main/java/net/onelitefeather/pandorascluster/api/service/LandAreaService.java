package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.service.result.CreateLandAreaResult;
import net.onelitefeather.pandorascluster.api.service.result.DeleteLandAreaResult;
import net.onelitefeather.pandorascluster.api.service.result.GetClaimedChunkResult;
import net.onelitefeather.pandorascluster.api.service.result.GetLandAreaResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LandAreaService {

    /**
     * @param chunk the chunk to claim
     * @param landArea the land area to add the chunk
     */
    default void claimChunk(@NotNull ClaimedChunk chunk, @Nullable LandArea landArea) {
        this.claimChunk(null, chunk, landArea);
    }

    void claimChunk(@Nullable Long id, @NotNull ClaimedChunk chunk, @Nullable LandArea landArea);


    /**
     * @param chunkIndex the chunk to remove from the land.
     **/
    boolean removeClaimedChunk(long chunkIndex);

    default boolean removeClaimedChunk(ClaimedChunk claimedChunk) {
        return removeClaimedChunk(claimedChunk.chunkIndex());
    }

    default boolean isChunkClaimed(@NotNull ClaimedChunk chunk) {
        return isChunkClaimed(chunk.chunkIndex());
    }

    default boolean isChunkClaimed(long chunkIndex) {
        return getClaimedChunk(chunkIndex) instanceof GetClaimedChunkResult.Found;
    }

    @NotNull
    GetClaimedChunkResult getClaimedChunk(long chunkIndex);

    @NotNull
    GetLandAreaResult getLandArea(long chunkIndex);

    @NotNull
    GetLandAreaResult getLandArea(String name, long id);

    @NotNull
    default GetLandAreaResult getLandArea(@NotNull ClaimedChunk chunk) {
        return getLandArea(chunk.chunkIndex());
    }

    DeleteLandAreaResult unclaimArea(LandArea landArea);

    CreateLandAreaResult createArea(Land land, String name, LandArea area, ClaimedChunk chunk);
}
