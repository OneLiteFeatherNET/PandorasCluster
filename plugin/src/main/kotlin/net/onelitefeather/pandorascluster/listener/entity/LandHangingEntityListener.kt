package net.onelitefeather.pandorascluster.listener.entity

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.enums.Permission
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import net.onelitefeather.pandorascluster.service.BukkitLandService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.hanging.HangingPlaceEvent

class LandHangingEntityListener(private val pandorasClusterApi: PandorasClusterApi,
                                private val bukkitLandService: BukkitLandService) : Listener, EntityUtils, ChunkUtils {

    @EventHandler
    fun handleHangingBreak(event: HangingBreakEvent) {
        if (event.cause == HangingBreakEvent.RemoveCause.ENTITY) return
        val entity = event.entity
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(entity.chunk)) ?: return
        event.isCancelled = land.getFlag(LandFlag.HANGING_BREAK).getValue<Boolean>() == false
    }

    @EventHandler
    fun handleHangingBreakByEntity(event: HangingBreakByEntityEvent) {

        val entity = event.entity
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(entity.chunk))
        val remover = event.remover

        if(land == null) {
            event.isCancelled = !hasPermission(remover, Permission.UNOWNED_CHUNK)
            return
        }

        if(land.hasMemberAccess(remover.uniqueId)) return
        event.isCancelled = land.getFlag(LandFlag.HANGING_BREAK).getValue<Boolean>() == false
    }

    @EventHandler
    fun handleHangingPlace(event: HangingPlaceEvent) {

        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.block.chunk))
        val player = event.player
        val landFlag = LandFlag.HANGING_PLACE

        if(land == null && player != null) {
            event.isCancelled = !hasPermission(player, Permission.UNOWNED_CHUNK)
            return
        }

        if(player != null && (hasPermission(player, landFlag) || land?.hasMemberAccess(player.uniqueId) == true)) return
        event.isCancelled = land?.getFlag(landFlag)?.getValue<Boolean>() == false
    }
}