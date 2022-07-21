package net.onelitefeather.pandorascluster.listener;

import net.onelitefeather.pandorascluster.api.PandorasClusterApi;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerConnectionListener(PandorasClusterApi api) implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        LandPlayer landPlayer = this.api.getLandPlayerService().getLandPlayer(event.getPlayer().getUniqueId());
        if (landPlayer != null) {
            this.api.getLandPlayerService().updateLandPlayer(landPlayer);
        }
    }
}
