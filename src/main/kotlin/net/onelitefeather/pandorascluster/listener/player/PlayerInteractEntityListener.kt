package net.onelitefeather.pandorascluster.listener.player;

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import org.bukkit.entity.Tameable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.player.PlayerBucketEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerShearEntityEvent
import org.bukkit.event.player.PlayerUnleashEntityEvent

class PlayerInteractEntityListener(val pandorasClusterApi: PandorasClusterApi) : Listener {

    @EventHandler
    fun handlePlayerBucketUse(event: PlayerBucketEntityEvent) {

        val land = pandorasClusterApi.getLand(event.entity.chunk)
        val landFlag = LandFlag.BUCKET_INTERACT

        if(land == null) {
            event.isCancelled = !event.player.hasPermission(landFlag)
            return
        }

        if (land.hasAccess(event.player.uniqueId)) return
        if (land.getLandFlag(landFlag).getValue<Boolean>() == true) return
        event.isCancelled = !event.player.hasPermission(landFlag)
    }

    @EventHandler
    fun handlePlayerInteractEntity(event: PlayerInteractEntityEvent) {

        val player = event.player
        val entity = event.rightClicked
        val land = this.pandorasClusterApi.getLand(entity.chunk) ?: return
        val landFlag = LandFlag.ENTITY_INTERACT

        if(land.hasAccess(player.uniqueId)) return
        if(land.getLandFlag(landFlag).getValue<Boolean>() == true) return
        event.isCancelled = !player.hasPermission(landFlag)
    }

    @EventHandler
    fun handlePlayerEntityShear(event: PlayerShearEntityEvent) {
        val land = pandorasClusterApi.getLand(event.entity.chunk)
        if (event.player.hasPermission(LandFlag.SHEAR_ENTITY)) return
        if (land == null || !land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handlePlayerLeashEntity(event: PlayerLeashEntityEvent) {
        val land = pandorasClusterApi.getLand(event.entity.chunk)
        if (event.player.hasPermission(LandFlag.ENTITY_LEASH)) return
        if (event.entity is Tameable && (event.entity as Tameable).ownerUniqueId == event.player.uniqueId) return
        if (land == null || !land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handlePlayerUnleashEntity(event: PlayerUnleashEntityEvent) {
        val land = pandorasClusterApi.getLand(event.entity.chunk)
        if (event.player.hasPermission(LandFlag.ENTITY_LEASH)) return
        if (event.entity is Tameable && (event.entity as Tameable).ownerUniqueId == event.player.uniqueId) return
        if (land == null || !land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }
}
