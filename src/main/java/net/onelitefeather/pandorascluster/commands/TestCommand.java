package net.onelitefeather.pandorascluster.commands;

import cloud.commandframework.annotations.CommandMethod;
import net.onelitefeather.pandorascluster.api.PandorasClusterApi;
import net.onelitefeather.pandorascluster.land.position.HomePosition;
import org.bukkit.entity.Player;

public class TestCommand {

    private final PandorasClusterApi pandorasClusterApi;

    public TestCommand(PandorasClusterApi pandorasClusterApi) {
        this.pandorasClusterApi = pandorasClusterApi;
    }

    @CommandMethod("fcksql")
    public void testHome(Player player) {


        HomePosition homePosition = this.pandorasClusterApi.getLandService().getHome(player.getUniqueId());
        if(homePosition == null) return;
        player.teleport(HomePosition.fromHomePosition(player.getWorld(), homePosition));

    }
}
