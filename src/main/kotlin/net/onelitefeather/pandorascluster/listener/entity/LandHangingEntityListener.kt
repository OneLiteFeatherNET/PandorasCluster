package net.onelitefeather.pandorascluster.listener.entity

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.service.LandService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.hanging.HangingPlaceEvent

class LandHangingEntityListener(private val pandorasClusterApi: PandorasClusterApi, private val landService: LandService) : Listener {

    @EventHandler
    fun handleHangingBreak(event: HangingBreakEvent) {
        if (event.cause == HangingBreakEvent.RemoveCause.ENTITY) return
        val entity = event.entity
        val land = pandorasClusterApi.getLand(entity.chunk) ?: return
        event.isCancelled = land.getLandFlag(LandFlag.HANGING_BREAK).getValue<Boolean>() == false
    }

    @EventHandler
    fun handleHangingBreakByEntity(event: HangingBreakByEntityEvent) {
        val entity = event.entity
        val land = pandorasClusterApi.getLand(entity.chunk) ?: return
        val remover = event.remover

        if(remover != null && (Permission.HANGING_BREAK.hasPermission(remover) || land.hasAccess(remover.uniqueId))) return
        event.isCancelled = land.getLandFlag(LandFlag.HANGING_BREAK).getValue<Boolean>() == false
    }

    @EventHandler
    fun handleHangingPlace(event: HangingPlaceEvent) {
        val land = pandorasClusterApi.getLand(event.block.chunk) ?: return
        val player = event.player

        if(player != null && (Permission.HANGING_BREAK.hasPermission(player) || land.hasAccess(player.uniqueId))) return
        event.isCancelled = land.getLandFlag(LandFlag.HANGING_PLACE).getValue<Boolean>() == false
    }
}