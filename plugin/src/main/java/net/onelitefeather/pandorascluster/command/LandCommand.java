package net.onelitefeather.pandorascluster.command;

import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.util.LocationUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

public class LandCommand {

    private final PandorasCluster pandorasCluster;

    public LandCommand(PandorasCluster pandorasCluster) {
        this.pandorasCluster = pandorasCluster;
    }

    @Command("land")
    public void commandLand(Player player) {

        var hasPlayerLand = this.pandorasCluster.getLandService().hasPlayerLand(player.getUniqueId());
        var landPlayer = this.pandorasCluster.getLandPlayerService().getLandPlayer(player.getUniqueId());

        if (hasPlayerLand) {
            player.sendMessage("You already have a land!");
        } else {
            var land = this.pandorasCluster.getLandService().createLand(
                    landPlayer,
                    LocationUtil.of(player.getLocation()),
                    LocationUtil.toClaimedChunk(player.getChunk()));
            player.sendMessage("Land created! " + land.getOwner().getName());
        }
    }

    @Command("land info")
    public void commandLandInfo(Player player) {

        var landArea = this.pandorasCluster.getLandAreaService().getLandArea(player.getChunk().getChunkKey());
        if (landArea == null) {
            player.sendMessage("No land area found!");
            return;
        }

        var land = landArea.getLand();
        if(land == null) {
            player.sendMessage("You´re not on a Land!");
            return;
        }
        player.sendMessage("Owner: %s".formatted(land.getOwner().getName()));
        player.sendMessage("Home: X: %s Y: %S Z: %s".formatted(land.getHome().getBlockX(), land.getHome().getBlockY(), land.getHome().getBlockZ()));
    }
}
