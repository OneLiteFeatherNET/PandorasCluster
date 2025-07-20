package net.onelitefeather.pandorascluster.listener;

import net.onelitefeather.pandorascluster.api.PandorasCluster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnectionListener implements Listener {

    private final PandorasCluster pandorasCluster;

    public PlayerConnectionListener(PandorasCluster pandorasCluster) {
        this.pandorasCluster = pandorasCluster;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(this.pandorasCluster.getLandPlayerService().playerExists(event.getPlayer().getUniqueId())) {
            return;
        }
        this.pandorasCluster.getLandPlayerService().createPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

}
