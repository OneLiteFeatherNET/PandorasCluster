package net.onelitefeather.pandorascluster.listener;

import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.player.ChunkPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerConnectionListener(PandorasClusterPlugin pandorasClusterPlugin) implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.pandorasClusterPlugin.getChunkPlayerService().loadOnlinePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        ChunkPlayer chunkPlayer = this.pandorasClusterPlugin.getChunkPlayerService().getChunkPlayer(player.getUniqueId());
        if (chunkPlayer != null) {
            this.pandorasClusterPlugin.getChunkPlayerService().updateChunkPlayer(chunkPlayer);
        }


        this.pandorasClusterPlugin.getChunkPlayerService().removeOnlinePlayer(player);
    }
}
