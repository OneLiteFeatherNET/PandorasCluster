package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.position.HomePosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface LandService {

    @NotNull
    List<Land> getLands();

    /**
     * @param homePosition the home position of the land
     * @param ownerId the new owner uuid
     */
    void updateLandHome(@NotNull HomePosition homePosition, @NotNull UUID ownerId);


    /**
     * @param land the land to update
     */
    void updateLand(@NotNull Land land);

    /**
     * @param chunk the chunk to claim
     * @param landArea the land area to add the chunk
     */
    void claimChunk(@NotNull ClaimedChunk chunk, @Nullable LandArea landArea);

    void addLandArea(Land land, String name, List<ClaimedChunk> chunks);

    /**
     * @param owner the owner of the land.
     * @param home the home position of the land
     * @param world the name of the world.
     * @param chunk the first claimed chunk
     */
    @Nullable
    Land createLand(@NotNull LandPlayer owner, @NotNull HomePosition home, @NotNull ClaimedChunk chunk, @NotNull String world);

    /**
     * @param land the land to unclaim.
     */
    void unclaimLand(@NotNull Land land);

    /**
     * @param chunkIndex the chunk to remove from the land.
     **/
    boolean removeClaimedChunk(long chunkIndex);

    @Nullable
    LandArea getLandArea(long chunkIndex);

    @Nullable
    default LandArea getLandArea(@NotNull ClaimedChunk chunk) {
        return getLandArea(chunk.getChunkIndex());
    }

    @Nullable
    Land getLand(@NotNull LandPlayer landPlayer);

    default boolean isChunkClaimed(@NotNull ClaimedChunk chunk) {
        return isChunkClaimed(chunk.getChunkIndex());
    }

    boolean isChunkClaimed(long chunkIndex);

    default boolean hasPlayerLand(@NotNull LandPlayer landPlayer) {
        return getLand(landPlayer) != null;
    }

    @Nullable
    ClaimedChunk getClaimedChunk(long chunkIndex);
}
