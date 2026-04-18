package net.onelitefeather.pandorascluster.command;

import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.service.CreateLandResult;
import net.onelitefeather.pandorascluster.api.service.GetLandAreaResult;
import net.onelitefeather.pandorascluster.api.service.GetLandResult;
import net.onelitefeather.pandorascluster.util.LocationUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

public class LandCommand {

    private final PandorasCluster pandorasCluster;

    public LandCommand(PandorasCluster pandorasCluster) {
        this.pandorasCluster = pandorasCluster;
    }

    @Command("land create")
    public void commandLand(Player player) {

        var hasPlayerLand = this.pandorasCluster.getLandService().hasPlayerLand(player.getUniqueId());
        var landPlayer = this.pandorasCluster.getLandPlayerService().getLandPlayer(player.getUniqueId());

        switch (this.pandorasCluster.getLandAreaService().getLandArea(player.getChunk().getChunkKey())) {
            case GetLandAreaResult.Found ignored -> {
                player.sendMessage("This chunk is already claimed!");
                return;
            }
            case GetLandAreaResult.Failed(String msg, Throwable ignored) -> {
                player.sendMessage("Database error: " + msg);
                return;
            }
            case GetLandAreaResult.NotFound ignored -> {
                // chunk is free — proceed with creation
            }
        }

        switch (this.pandorasCluster.getLandService().createLand(
                landPlayer,
                LocationUtil.of(player.getLocation()),
                LocationUtil.toClaimedChunk(player.getChunk()))) {
            case CreateLandResult.Created(Land land) ->
                    player.sendMessage("Land created! " + land.getId());
            case CreateLandResult.Failed(String msg, Throwable ignored) ->
                    player.sendMessage("Failed to create land: " + msg);
        }
    }

    @Command("land info")
    public void commandLandInfo(Player player) {

        LandArea landArea = switch (this.pandorasCluster.getLandAreaService().getLandArea(player.getChunk().getChunkKey())) {
            case GetLandAreaResult.Found(LandArea area) -> area;
            case GetLandAreaResult.NotFound ignored -> {
                player.sendMessage("No land area found!");
                yield null;
            }
            case GetLandAreaResult.Failed(String msg, Throwable ignored) -> {
                player.sendMessage("Database error: " + msg);
                yield null;
            }
        };
        if (landArea == null) return;

        Long landId = landArea.getLandId();
        if (landId == null) {
            player.sendMessage("You're not on a Land!");
            return;
        }

        switch (this.pandorasCluster.getLandService().getLand(landId)) {
            case GetLandResult.Found(Land land) -> {
                player.sendMessage("Owner: %s".formatted(land.getOwner().getName()));
                player.sendMessage("Home: X: %s Y: %S Z: %s".formatted(
                        land.getHome().getBlockX(),
                        land.getHome().getBlockY(),
                        land.getHome().getBlockZ()));
            }
            case GetLandResult.NotFound ignored ->
                    player.sendMessage("You're not on a Land!");
            case GetLandResult.Failed(String msg, Throwable ignored) ->
                    player.sendMessage("Database error: " + msg);
        }
    }
}
