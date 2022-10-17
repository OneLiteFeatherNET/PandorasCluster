package net.onelitefeather.pandorascluster.listener.player;

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.spigotmc.event.player.PlayerSpawnLocationEvent

class PlayerLocationListener(val pandorasClusterApi: PandorasClusterApi) : Listener {

    val world: World = Bukkit.getWorlds()[0]

    @EventHandler
    fun handlePlayerMovement(event: PlayerMoveEvent) {
        val player = event.player
        if (!event.hasExplicitlyChangedBlock()) return
        val toLand = pandorasClusterApi.getLand(event.to.chunk)
        if (toLand != null) {
            if (Permission.LAND_ENTRY_DENIED.hasPermission(player)) return
            if (!toLand.isBanned(player.uniqueId)) return
            event.to = event.from
        }
    }

    @EventHandler
    fun handlePlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        val to = event.to
        val from = event.from
        val land = pandorasClusterApi.getLand(to.chunk) ?: pandorasClusterApi.getLand(from.chunk) ?: return
        if (Permission.LAND_ENTRY_DENIED.hasPermission(player)) return
        if (land.isBanned(player.uniqueId)) {
            event.to = event.from
        }
    }

    @EventHandler
    fun handlePlayerRespawn(event: PlayerRespawnEvent) {
        val land = pandorasClusterApi.getLand(event.respawnLocation.chunk)
        if (Permission.LAND_ENTRY_DENIED.hasPermission(event.player)) return
        if (land == null || !land.isBanned(event.player.uniqueId)) return
        event.respawnLocation = world.spawnLocation.toCenterLocation()
    }

    @EventHandler
    fun handlePlayerSpawn(event: PlayerSpawnLocationEvent) {
        val land = pandorasClusterApi.getLand(event.spawnLocation.chunk)
        if (Permission.LAND_ENTRY_DENIED.hasPermission(event.player)) return
        if (land == null || !land.isBanned(event.player.uniqueId)) return
        event.spawnLocation = world.spawnLocation.toCenterLocation()
    }

}
