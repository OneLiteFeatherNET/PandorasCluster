package net.onelitefeather.pandorascluster.listener

import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.service.LandService
import org.bukkit.block.Container
import org.bukkit.block.data.type.Farmland
import org.bukkit.block.data.type.RespawnAnchor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent

class LandPlayerListener(private val landService: LandService) :
    Listener {

    @EventHandler
    fun handlePlayerMovement(event: PlayerMoveEvent) {
        val player = event.player
        if (!event.hasExplicitlyChangedBlock()) return
        val toLand = landService.getFullLand(event.to.chunk)
        if (toLand != null) {
            if (Permission.LAND_ENTRY_DENIED.hasPermission(player)) return
            if (!toLand.isBanned(player.uniqueId)) return
            event.isCancelled = true
        }
    }

    @EventHandler
    fun handlePlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        val to = event.to
        val from = event.from
        val land = landService.getFullLand(to.chunk) ?: landService.getFullLand(from.chunk) ?: return
        if (Permission.LAND_ENTRY_DENIED.hasPermission(player)) return
        if (land.isBanned(player.uniqueId)) {
            event.to = event.from
        }
    }

    @Suppress("kotlin:S3776")
    @EventHandler
    fun handlePlayerInteract(event: PlayerInteractEvent) {
        val clickedBlock = event.clickedBlock ?: return
        val land = landService.getFullLand(clickedBlock.chunk) ?: return
        if (land.hasAccess(event.player.uniqueId)) return

        val blockData = clickedBlock.blockData
        event.isCancelled = if (event.material.isInteractable) {
            if (event.player.hasPermission(LandFlag.USE)) return
            true
        } else if (blockData is Farmland && event.action == Action.PHYSICAL) {
            val landFlag = landService.getLandFlag(LandFlag.FARMLAND_DESTROY, land) ?: return
            if (landFlag.getValue<Boolean>() == true) return
            if (Permission.INTERACT_FARMLAND.hasPermission(event.player)) return
            true
        } else if (clickedBlock.state is Container) {
            if (Permission.INTERACT_CONTAINERS.hasPermission(event.player)) return
            true
        } else if (blockData is RespawnAnchor && event.action == Action.RIGHT_CLICK_BLOCK && blockData.charges == blockData.maximumCharges) {
            val landFlag = landService.getLandFlag(LandFlag.EXPLOSIONS, land) ?: return
            if (landFlag.getValue<Boolean>() == true) return
            if (event.player.hasPermission(LandFlag.EXPLOSIONS)) return
            true
        } else false
    }
}