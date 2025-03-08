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


    @Nullable
    Land getLand(@NotNull LandPlayer landPlayer);


    default boolean hasPlayerLand(@NotNull LandPlayer landPlayer) {
        return getLand(landPlayer) != null;
    }
}
