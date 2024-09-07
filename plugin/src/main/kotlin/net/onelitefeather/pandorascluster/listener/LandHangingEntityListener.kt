package net.onelitefeather.pandorascluster.listener

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
        val landFlag = pandorasClusterApi.getLandFlag(LandFlag.HANGING_BREAK, land) ?: return
        if (landFlag.getValue<Boolean>() == true) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleHangingBreakByEntity(event: HangingBreakByEntityEvent) {
        val entity = event.entity
        val land = pandorasClusterApi.getLand(entity.chunk) ?: return
        val landFlag = pandorasClusterApi.getLandFlag(LandFlag.HANGING_BREAK, land) ?: return
        if(Permission.HANGING_BREAK.hasPermission(event.remover!!)) return
        if (landFlag.getValue<Boolean>() == true) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleHangingPlace(event: HangingPlaceEvent) {
        val land = pandorasClusterApi.getLand(event.block.chunk) ?: return
        val landFlag = pandorasClusterApi.getLandFlag(LandFlag.HANGING_PLACE, land) ?: return
        if(Permission.HANGING_PLACE.hasPermission(event.player!!)) return
        if (landFlag.getValue<Boolean>() == true) return
        event.isCancelled = true
    }
}