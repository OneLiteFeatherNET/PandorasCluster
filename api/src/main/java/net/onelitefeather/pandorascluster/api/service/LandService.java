package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandWorld;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.position.HomePosition;
import net.onelitefeather.pandorascluster.api.service.result.land.CreateLandResult;
import net.onelitefeather.pandorascluster.api.service.result.land.GetLandResult;
import net.onelitefeather.pandorascluster.api.service.result.world.CreateLandWorldResult;
import net.onelitefeather.pandorascluster.api.service.result.world.DeleteLandWorldResult;
import net.onelitefeather.pandorascluster.api.service.result.world.GetLandWorldResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface LandService {

    @NotNull
    List<Land> getLands();

    /**
     *
     * @param world the world to get lands for
     * @return a list of {@link Land}'s in the given world. If the world has no lands, returns an empty list.
     */
    @NotNull
    List<Land> getLands(LandWorld world);

    /**
     * @param id the primary key of the land
     * @return a {@link GetLandResult} variant — callers must pattern-match on
     *         {@code Found}, {@code NotFound} and {@code Failed}
     */
    @NotNull
    GetLandResult getLand(@NotNull Long id);

    @NotNull
    GetLandResult getLand(@NotNull UUID ownerId, long landId);

    /**
     * @param homePosition the home position of the land
     * @param ownerId      the new owner uuid
     */
    void updateLandHome(@NotNull HomePosition homePosition, @NotNull UUID ownerId);

    /**
     * @param land the land to update
     */
    void updateLand(@NotNull Land land);

    /**
     * @param owner the owner of the land.
     * @param home  the home position of the land
     * @param chunk the first claimed chunk
     * @return a {@link CreateLandResult} variant — exhaustive switch forces
     *         callers to handle {@code Created} and {@code Failed}
     */
    @NotNull
    CreateLandResult createLand(@NotNull LandPlayer owner, @NotNull LandWorld world, @NotNull HomePosition home, @NotNull ClaimedChunk chunk);

    /**
     * @param land the land to unclaim.
     */
    void unclaimLand(@NotNull Land land);

    CreateLandWorldResult createLandWorld(UUID worldId, String name);

    DeleteLandWorldResult deleteLandWorld(UUID worldId);

    GetLandWorldResult getLandWorld(UUID worldId);

    GetLandWorldResult getLandWorld(long id);

    default boolean isLandWorld(UUID uuid) {
        return getLandWorld(uuid) instanceof GetLandWorldResult.Found;
    }

    default DeleteLandWorldResult deleteLandWorld(LandWorld world) {
        return this.deleteLandWorld(world.worldId());
    }

    default boolean hasPlayerLand(@NotNull UUID uuid) {
        return getLand(uuid , 1) instanceof GetLandResult.Found;
    }

    default boolean hasPlayerLand(@NotNull LandPlayer landPlayer) {
        return hasPlayerLand(landPlayer.uniqueId());
    }
}
