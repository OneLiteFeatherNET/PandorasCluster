package net.onelitefeather.pandorascluster.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import net.kyori.adventure.text.Component;
import net.onelitefeather.pandorascluster.api.PandorasClusterApi;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
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
            if (land != null) {

                if (!land.isOwner(player.getUniqueId())) {
                    player.sendMessage(Component.text("You´re not the Owner from this Land!"));
                    return;
                }

                this.api.getLandService().merge(land, playerChunk);
                player.sendMessage(Component.text("You´ve successfully merged this land!"));
                player.sendMessage(String.format("DEBUG: Connected with Land X: %d Z: %d", land.getX(), land.getZ()));
                
            } else {
                this.api.getLandService().createLand(landPlayer, player, playerChunk);
                player.sendMessage(Component.text("You´ve successfully claimed this land!"));
            }
        });
    }
}
