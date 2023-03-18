package net.onelitefeather.pandorascluster.listener.player

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerConnectionListener(val api: PandorasClusterApi) : Listener {

    @EventHandler
    fun handlePlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        val landPlayer = api.getLandPlayer(player.uniqueId)
        if(landPlayer != null && !landPlayer.name.equals(player.name, true)) {
            api.getLandPlayerService().updateLandPlayer(landPlayer.copy(name = player.name))
        }

        if(api.registerPlayer(player.uniqueId, player.name)) {
            player.sendMessage("Your playerdata was successfully created!")
        }

    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val landPlayer = api.getLandPlayer(event.player.uniqueId) ?: return
        api.getLandPlayerService().updateLandPlayer(landPlayer)
        api.getLandService().disableBorderView(event.player)
    }
}