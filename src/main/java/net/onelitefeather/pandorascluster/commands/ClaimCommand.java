package net.onelitefeather.pandorascluster.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import com.google.common.collect.Maps;
import net.kyori.adventure.text.Component;
import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.chunk.WorldChunk;
import net.onelitefeather.pandorascluster.service.LandService1;
import net.onelitefeather.pandorascluster.builder.LandBuilder;
import net.onelitefeather.pandorascluster.player.ChunkPlayer;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClaimCommand {

    private final PandorasClusterPlugin plugin;
    private final LandService1 landService1;

    public ClaimCommand(@NotNull PandorasClusterPlugin plugin) {
        this.plugin = plugin;
        this.landService1 = plugin.getWorldChunkManager();
    }

    @CommandMethod("land claim")
    @CommandDescription("Claim a free chunk")
    public void execute(@NotNull Player player) {

        ChunkPlayer chunkPlayer = this.plugin.getChunkPlayerService().getChunkPlayer(player.getUniqueId());
        if (chunkPlayer == null) {
            player.sendMessage("Could not find your player data.");
            return;
        }

        Chunk playerChunk = player.getChunk();

        if (this.landService1.isChunkClaimed(player.getChunk())) {
            player.sendMessage(Component.text("Der Chunk wurde bereits geclaimt!"));
            return;
        }

        this.landService1.findConnectedChunk(player, worldChunk -> {
            if (worldChunk != null) {

                this.landService1.merge(worldChunk, playerChunk);
                player.sendMessage(Component.text("The Chunk was successfully merged with the chunk x: "
                        + worldChunk.getX() + " z: " + worldChunk.getZ()));

            } else {

                WorldChunk claimed = new LandBuilder().
                        owner(player.getUniqueId()).
                        chunkX(playerChunk.getX()).
                        chunkZ(playerChunk.getZ()).
                        world(player.getWorld()).
                        home(player.getLocation()).
                        chunkRoles(Maps.newHashMap()).
                        mergedChunks(List.of()).build();

                this.landService1.create(claimed);
                player.sendMessage(Component.text("created!"));
            }

            chunkPlayer.decreaseAvailableChunkClaims(1);
            chunkPlayer.increaseChunkCount(1);
            this.plugin.getChunkPlayerService().updateChunkPlayer(chunkPlayer);
        });
    }
}
