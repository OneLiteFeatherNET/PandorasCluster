package net.onelitefeather.pandorascluster;

import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import net.onelitefeather.pandorascluster.service.EntityDataStoreService;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface PandorasClusterApi {

    PandorasClusterPlugin getPlugin();

    EntityDataStoreService getEntityDataStoreService();

    boolean hasPlayerLand(Player player);

    boolean hasPlayerLand(UUID playerId);

    boolean isChunkClaimed(Chunk chunk);

    boolean isChunkClaimed(int x, int z);

    /**
     * @return A list of all {@link Land}'s
     */
    List<Land> getLands();

    LandPlayer getLandPlayer(@NotNull Player player);

    LandPlayer getLandPlayer(@NotNull UUID uuid);

    LandPlayer getLandPlayer(@NotNull String name);

    /**
     * @param player the player
     * @return A list of all {@link Land}'s where the player has access.
     */
    List<Land> getLands(Player player);

    boolean hasSameOwner(Land land, Land other);

}
