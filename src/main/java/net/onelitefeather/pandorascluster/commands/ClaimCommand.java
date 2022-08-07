package net.onelitefeather.pandorascluster.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import net.kyori.adventure.text.Component;
import net.onelitefeather.pandorascluster.api.PandorasClusterApi;
import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import net.onelitefeather.pandorascluster.util.ChunkUtil;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record ClaimCommand(PandorasClusterApi api) {

    public ClaimCommand(@NotNull PandorasClusterApi api) {
        this.api = api;
    }

    @CommandMethod("land claim")
    @CommandDescription("Claim a free chunk")
    public void execute(@NotNull Player player) {

        LandPlayer landPlayer = this.api.getLandPlayer(player.getUniqueId());
        if (landPlayer == null) {
            player.sendMessage("Could not find your player data.");
            return;
        }

        Chunk playerChunk = player.getChunk();

        if (this.api.isChunkClaimed(player.getChunk())) {
            player.sendMessage(Component.text("This chunk was already claimed!"));
            return;
        }

        this.api.getLandService().findConnectedChunk(player, land -> {

            var chunkX = playerChunk.getX();
            var chunkZ = playerChunk.getZ();

            Chunk claimedChunk = null;

            for (int x = -2; x < 2 && claimedChunk == null; x++) {
                for (int z = -2; z < 2 && claimedChunk == null; z++) {
                    Chunk chunk = player.getWorld().getChunkAt(x + chunkX, z + chunkZ);
                    if (this.api.isChunkClaimed(chunk)) {
                        claimedChunk = chunk;
                    }
                }
            }

            if (land != null) {

                if (claimedChunk != null) {
                    Land claimedLand = this.api.getLand(claimedChunk);
                    if (claimedLand != null && !ChunkUtil.hasSameOwner(land, claimedLand)) {
                        player.sendMessage(Component.text("distance"));
                        return;
                    }
                }

                if (!land.isOwner(player.getUniqueId())) {
                    player.sendMessage(Component.text("You´re not the Owner from this Land!"));
                    return;
                }

                this.api.getLandService().addChunkPlaceholder(playerChunk, land);
                player.sendMessage(Component.text("You´ve successfully merged this land!"));
                player.sendMessage(String.format("DEBUG: Connected with Land X: %d Z: %d", land.getX(), land.getZ()));

            } else {
                if (this.api.hasPlayerLand(player)) {
                    player.sendMessage(Component.text("Du besitzt bereits schon ein Land."));
                } else {
                    this.api.getLandService().createLand(landPlayer, player, playerChunk);
                    player.sendMessage(Component.text("You´ve successfully claimed this land."));
                }
            }
        });
    }


}
