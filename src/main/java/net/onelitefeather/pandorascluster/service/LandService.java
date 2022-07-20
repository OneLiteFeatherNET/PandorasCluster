package net.onelitefeather.pandorascluster.service;

import net.onelitefeather.pandorascluster.PandorasClusterApi;
import net.onelitefeather.pandorascluster.builder.LandBuilder;
import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LandService implements Listener {

    private final PandorasClusterApi pandorasClusterApi;
    private final Map<OfflinePlayer, Land> playerLands;

    private final Map<Chunk, Land> claimedChunks;


    public LandService(PandorasClusterApi pandorasClusterApi) {
        this.pandorasClusterApi = pandorasClusterApi;
        this.playerLands = new HashMap<>();
        this.claimedChunks = new HashMap<>();
    }

    @NotNull
    public Map<OfflinePlayer, Land> getPlayerLands() {
        return playerLands;
    }

    public void createLand(@NotNull LandPlayer owner, @NotNull Player player, @NotNull Chunk chunk) {

        Land land = new LandBuilder().
                owner(owner).
                home(player.getLocation()).
                world(player.getWorld()).
                chunkX(chunk.getX()).
                chunkZ(chunk.getZ()).
                members(List.of()).
                mergedChunks(List.of()).
                withFlags(List.of()).
                build();

        this.playerLands.put(player, land);
        this.claimedChunks.put(chunk, land);
        //TODO: Store in database
    }

    public void deletePlayerLand(@NotNull Player player) {

        Land land = this.playerLands.remove(player);

        World world = player.getServer().getWorld(land.getWorld());
        if (world == null) return;

        Chunk chunk = world.getChunkAt(land.getX(), land.getZ());
        this.claimedChunks.remove(chunk);
        //TODO: Delete from database
    }

    public boolean isChunkClaimed(@NotNull Chunk chunk) {
        return this.claimedChunks.containsKey(chunk);
    }

    @EventHandler
    public void handleEntityChangeBlock(EntityChangeBlockEvent event) {
        var land = this.claimedChunks.get(event.getBlock().getChunk());
        if (land != null) land.getFlagHandler().handleEntityChangeBlock(event);
    }
}

