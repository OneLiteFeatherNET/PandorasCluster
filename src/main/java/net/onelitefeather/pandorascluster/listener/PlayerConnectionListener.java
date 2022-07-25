package net.onelitefeather.pandorascluster.listener;

import net.onelitefeather.pandorascluster.api.PandorasClusterApi;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.imageio.plugins.tiff.TIFFTagSet;

public record PlayerConnectionListener(PandorasClusterApi api) implements Listener {

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.api.registerPlayer(player.getUniqueId(), player.getName(), success -> {
            if(success) {
                player.sendMessage("Your playerdata was successfully created!");
            } else {
                player.sendMessage("Your playerdata could not be created!");
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        LandPlayer landPlayer = this.api.getLandPlayerService().getLandPlayer(event.getPlayer().getUniqueId());
        if (landPlayer != null) {
            this.api.getLandPlayerService().updateLandPlayer(landPlayer);
        }
    }
}
