package net.onelitefeather.pandorascluster.listener

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerConnectionListener(private val api: PandorasClusterApi) : Listener {

    @EventHandler
    fun handlePlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        this.api.registerPlayer(player.uniqueId, player.name) { success: Boolean ->
            if (success) {
                player.sendMessage("Your playerdata was successfully created!")
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val landPlayer = api.getLandPlayer(event.player.uniqueId) ?: return
        api.getLandPlayerService().updateLandPlayer(landPlayer)
    }
}