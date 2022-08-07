package net.onelitefeather.pandorascluster.listener

import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.service.LandService
import net.onelitefeather.pandorascluster.service.LandFlagService
import org.bukkit.block.Container
import org.bukkit.block.data.Powerable
import org.bukkit.block.data.type.Farmland
import org.bukkit.block.data.type.RespawnAnchor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent

class LandPlayerListener(private val landService: LandService, private val landFlagService: LandFlagService) :
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
        var land = landService.getFullLand(to.chunk)
        if (land == null) {
            land = landService.getFullLand(from.chunk)
        }
        if (land != null) {
            if (Permission.LAND_ENTRY_DENIED.hasPermission(player)) return
            if (land.isBanned(player.uniqueId)) {
                event.to = event.from
            }
        }
    }

    @EventHandler
    fun handlePlayerInteract(event: PlayerInteractEvent) {

        val clickedBlock = event.clickedBlock ?: return
        val player = event.player

        val land = landService.getFullLand(clickedBlock.chunk) ?: return
        val blockData = clickedBlock.blockData
        var cancel = false

        if (blockData is Farmland && event.action == Action.PHYSICAL) {
            val landFlag = landFlagService.getFlag(LandFlag.FARMLAND_DESTROY, land)?: return
            if (landFlag.getValue()) return
            if (land.hasAccess(player.uniqueId)) return

            if (Permission.INTERACT_FARMLAND.hasPermission(player)) return
            cancel = true
        }

        if (blockData is Powerable) {

            val landFlag = landFlagService.getFlag(LandFlag.REDSTONE, land)?: return
            if (true && landFlag.getValue()) return

            if (land.hasAccess(player.uniqueId)) return
            if (Permission.USE_REDSTONE.hasPermission(player)) return
            cancel = true
        }

        if (clickedBlock.state is Container) {
            if (land.hasAccess(player.uniqueId)) return
            if (Permission.INTERACT_CONTAINERS.hasPermission(player)) return
            cancel = true
        }

        if (blockData is RespawnAnchor && event.action == Action.RIGHT_CLICK_BLOCK) {
            if(blockData.charges == blockData.maximumCharges) {
                val landFlag = landFlagService.getFlag(LandFlag.EXPLOSIONS, land)?: return
                if (landFlag.getValue()) return
                if (Permission.EXPLOSION.hasPermission(player)) return
                cancel = true
            }
        }

        event.isCancelled = cancel
    }
}