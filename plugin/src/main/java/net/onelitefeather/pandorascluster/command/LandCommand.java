package net.onelitefeather.pandorascluster.command;

import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.land.LandWorld;
import net.onelitefeather.pandorascluster.api.service.result.land.CreateLandResult;
import net.onelitefeather.pandorascluster.api.service.result.land.GetLandAreaResult;
import net.onelitefeather.pandorascluster.api.service.result.land.GetLandResult;
import net.onelitefeather.pandorascluster.api.service.result.world.GetLandWorldResult;
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

        switch (this.pandorasCluster.getLandWorld(player.getWorld().getUID())) {
            case GetLandWorldResult.Found(LandWorld world) -> {
                switch (this.pandorasCluster.getLandService().createLand(
                        landPlayer,
                        world,
                        LocationUtil.of(player.getLocation()),
                        LocationUtil.toClaimedChunk(player.getChunk()))) {
                    case CreateLandResult.Created(Land land) -> player.sendMessage("Land created! " + land.id());
                    case CreateLandResult.Failed(String msg, Throwable ignored) ->
                            player.sendMessage("Failed to create land: " + msg);
                }
            }
            case GetLandWorldResult.Failed(String msg, Throwable ignored) -> {
                player.sendMessage("Failed to receive the land world for : " + player.getWorld().getName());
                player.sendRichMessage("<red>Error: %s</red>".formatted(msg));
            }

            case GetLandWorldResult.NotFound() -> player.sendRichMessage("<red>The world %s is not a Landworld</red>".formatted(player.getWorld().getName()));
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

        Long landId = landArea.landId();
        if (landId == null) {
            player.sendMessage("You're not on a Land!");
            return;
        }

        switch (this.pandorasCluster.getLandService().getLand(landId)) {
            case GetLandResult.Found(Land land) -> {
                player.sendMessage("Owner: %s".formatted(land.owner().name()));
                player.sendMessage("Home: X: %s Y: %S Z: %s".formatted(
                        land.home().getBlockX(),
                        land.home().getBlockY(),
                        land.home().getBlockZ()));
            }
            case GetLandResult.NotFound ignored -> player.sendMessage("You're not on a Land!");
            case GetLandResult.Failed(String msg, Throwable ignored) -> player.sendMessage("Database error: " + msg);
        }
    }
}
