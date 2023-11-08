package net.onelitefeather.pandorascluster.listener.player;

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.extensions.isPetOwner
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import org.bukkit.entity.AbstractVillager
import org.bukkit.entity.Allay
import org.bukkit.entity.Cow
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
    fun handlePlayerEntityInteract(event: PlayerInteractEntityEvent) {

        val player = event.player
        val entity = event.rightClicked
        val land = pandorasClusterApi.getLand(entity.chunk) ?: return

        event.isCancelled = when (entity) {

            is Tameable -> {
                if(!entity.isTamed) {
                    if(land.getLandFlag(LandFlag.ENTITY_MOUNT).getValue<Boolean>() == true) return
                    !player.hasPermission(LandFlag.ENTITY_MOUNT)
                } else {
                    !player.isPetOwner(entity)
                }
            }

            is Allay -> { !land.hasAccess(player.uniqueId) }
            is Cow -> {
                if(land.hasAccess(player.uniqueId)) return
                if(land.getLandFlag(LandFlag.BUCKET_INTERACT).getValue<Boolean>() == true) return
                !player.hasPermission(LandFlag.BUCKET_INTERACT)
            }

            is AbstractVillager -> {
                if(land.hasAccess(player.uniqueId)) return
                if(land.getLandFlag(LandFlag.VILLAGER_INTERACT).getValue<Boolean>() == true) return
                !player.hasPermission(LandFlag.VILLAGER_INTERACT)
            }

            else -> {
                false
            }
        }
    }

    @EventHandler
    fun handlePlayerBucketUse(event: PlayerBucketEntityEvent) {

        val land = pandorasClusterApi.getLand(event.entity.chunk)
        if (land == null) {
            event.isCancelled = !event.player.hasPermission( LandFlag.BUCKET_INTERACT)
            return
        }

        if (land.hasAccess(event.player.uniqueId)) return
        if (land.getLandFlag( LandFlag.BUCKET_INTERACT).getValue<Boolean>() == true) return
        event.isCancelled = !event.player.hasPermission( LandFlag.BUCKET_INTERACT)
    }

    @EventHandler
    fun handlePlayerEntityShear(event: PlayerShearEntityEvent) {

        val land = pandorasClusterApi.getLand(event.entity.chunk)
        if(land == null) {
            event.isCancelled = !event.player.hasPermission(LandFlag.SHEAR_ENTITY)
            return
        }

        if (land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = !event.player.hasPermission(LandFlag.SHEAR_ENTITY)
    }

    @EventHandler
    fun handlePlayerLeashEntity(event: PlayerLeashEntityEvent) {
        val land = pandorasClusterApi.getLand(event.entity.chunk) ?: return
        if (event.entity is Tameable && (event.entity as Tameable).ownerUniqueId == event.player.uniqueId) return
        if (land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = !event.player.hasPermission(LandFlag.ENTITY_LEASH)
    }

    @EventHandler
    fun handlePlayerUnleashEntity(event: PlayerUnleashEntityEvent) {
        val land = pandorasClusterApi.getLand(event.entity.chunk) ?: return
        if (event.entity is Tameable && (event.entity as Tameable).ownerUniqueId == event.player.uniqueId) return
        if (land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = !event.player.hasPermission(LandFlag.ENTITY_LEASH)
    }
}
