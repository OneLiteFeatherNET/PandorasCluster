package net.onelitefeather.pandorascluster.listener.player

import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.PandorasClusterPlugin
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerConnectionListener(val api: PandorasClusterApi, private val plugin: PandorasClusterPlugin) : Listener {

    @EventHandler
    fun handlePlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        val landPlayer = api.getLandPlayerService().getLandPlayer(player.uniqueId)
        if(landPlayer != null && !landPlayer.name.equals(player.name, true)) {
            api.getLandPlayerService().updateLandPlayer(landPlayer.copy(name = player.name))
        }

        if(api.getLandPlayerService().createPlayer(player.uniqueId, player.name)) {
            player.sendMessage(Component.translatable("player-data-created").arguments(api.pluginPrefix()))
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val landPlayer = api.getLandPlayerService().getLandPlayer(event.player.uniqueId) ?: return
        api.getLandPlayerService().updateLandPlayer(landPlayer)

        if(plugin.bukkitLandService.showBorderOfLand.contains(event.player)) {
            plugin.bukkitLandService.toggleShowBorder(event.player)
        }
    }
}