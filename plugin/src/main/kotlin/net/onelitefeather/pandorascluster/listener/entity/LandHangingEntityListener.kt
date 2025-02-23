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
        val land = pandorasClusterApi.getLandService().getLand(entity.chunk.chunkKey) ?: return
        event.isCancelled = !land.hasFlag(LandFlag.HANGING_BREAK)
    }

    @EventHandler
    fun handleHangingBreakByEntity(event: HangingBreakByEntityEvent) {

        val entity = event.entity
        val land = pandorasClusterApi.getLandService().getLand(entity.chunk.chunkKey)
        val remover = event.remover

        if(land == null) {
            event.isCancelled = !hasPermission(remover, Permission.UNOWNED_CHUNK)
            return
        }

        event.isCancelled = !land.hasMemberAccess(remover.uniqueId, LandFlag.HANGING_BREAK)
    }

    @EventHandler
    fun handleHangingPlace(event: HangingPlaceEvent) {

        val land = pandorasClusterApi.getLandService().getLand(event.block.chunk.chunkKey)
        val player = event.player
        val landFlag = LandFlag.HANGING_PLACE

        if(land == null && player != null) {
            event.isCancelled = !hasPermission(player, Permission.UNOWNED_CHUNK)
            return
        }

        if(land == null) return
        if(player != null) {
            event.isCancelled = !land.hasMemberAccess(player.uniqueId, LandFlag.HANGING_PLACE)
        } else {
            event.isCancelled = !land.hasFlag(LandFlag.HANGING_PLACE)
        }
    }
}