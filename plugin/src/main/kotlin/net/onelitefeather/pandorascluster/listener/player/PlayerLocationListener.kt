package net.onelitefeather.pandorascluster.listener.player

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.enums.Permission
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.spigotmc.event.player.PlayerSpawnLocationEvent

class PlayerLocationListener(val pandorasClusterApi: PandorasClusterApi) : Listener, ChunkUtils {

    val world: World = Bukkit.getWorlds()[0]
    private val allowedReasons = listOf(
        PlayerTeleportEvent.TeleportCause.PLUGIN,
        PlayerTeleportEvent.TeleportCause.COMMAND,
        PlayerTeleportEvent.TeleportCause.UNKNOWN
    )

    @EventHandler
    fun handlePlayerMovement(event: PlayerMoveEvent) {
        if (!event.hasExplicitlyChangedBlock()) return
        val player = event.player
        val toLand = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.to.chunk)) ?: return
        if (canEnterLand(player, toLand)) return

        val fromLand = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.from.chunk))
        if (fromLand != null && toLand.isBanned(player.uniqueId)) {
            event.to = world.spawnLocation
            return
        }

        event.to = event.to.set(event.from.blockX.toDouble(), event.to.y, event.from.blockZ.toDouble())
    }

    @EventHandler
    fun handlePlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        val to = event.to
        val from = event.from
        if (allowedReasons.contains(event.cause)) return

        val fromLand = pandorasClusterApi.getLandService().getLand(toClaimedChunk(from.chunk))
        val toLand = pandorasClusterApi.getLandService().getLand(toClaimedChunk(to.chunk))
        val land = toLand ?: fromLand ?: return

        if (canEnterLand(player, land)) return
        event.to = event.from
    }

    @EventHandler
    fun handlePlayerRespawn(event: PlayerRespawnEvent) {
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.respawnLocation.chunk))
        if (event.player.hasPermission(Permission.LAND_ENTRY_DENIED.permissionNode)) return
        if (land == null || !land.isBanned(event.player.uniqueId)) return
        event.respawnLocation = world.spawnLocation.toCenterLocation()
    }

    @EventHandler
    fun handlePlayerSpawn(event: PlayerSpawnLocationEvent) {
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.spawnLocation.chunk))
        if (event.player.hasPermission(Permission.LAND_ENTRY_DENIED.permissionNode)) return
        if (land == null || !land.isBanned(event.player.uniqueId)) return
        event.spawnLocation = world.spawnLocation.toCenterLocation()
    }

}
