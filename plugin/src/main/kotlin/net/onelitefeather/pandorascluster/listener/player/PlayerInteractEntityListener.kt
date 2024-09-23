package net.onelitefeather.pandorascluster.listener.player;

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
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

class PlayerInteractEntityListener(val pandorasClusterApi: PandorasClusterApi) : Listener, EntityUtils, ChunkUtils {

    @EventHandler
    fun handlePlayerEntityInteract(event: PlayerInteractEntityEvent) {

        val player = event.player
        val entity = event.rightClicked
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(entity.chunk)) ?: return

        event.isCancelled = when (entity) {

            is Allay -> { !land.hasMemberAccess(player.uniqueId) }
            is Cow -> {
                if(land.hasMemberAccess(player.uniqueId)) return
                if(land.getFlag(LandFlag.BUCKET_INTERACT).getValue<Boolean>() == true) return
                !hasPermission(player, LandFlag.BUCKET_INTERACT)
            }

            is AbstractVillager -> {
                if(land.hasMemberAccess(player.uniqueId)) return
                if(land.getFlag(LandFlag.VILLAGER_INTERACT).getValue<Boolean>() == true) return
                !hasPermission(player, LandFlag.VILLAGER_INTERACT)
            }

            else -> {
                false
            }
        }
    }

    @EventHandler
    fun handlePlayerBucketUse(event: PlayerBucketEntityEvent) {

        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.entity.chunk))
        if (land == null) {
            event.isCancelled = !hasPermission(event.player, LandFlag.BUCKET_INTERACT)
            return
        }

        if (land.hasMemberAccess(event.player.uniqueId)) return
        if (land.getFlag( LandFlag.BUCKET_INTERACT).getValue<Boolean>() == true) return
        event.isCancelled = !hasPermission(event.player, LandFlag.BUCKET_INTERACT)
    }

    @EventHandler
    fun handlePlayerEntityShear(event: PlayerShearEntityEvent) {

        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.entity.chunk))
        if(land == null) {
            event.isCancelled = !hasPermission(event.player, LandFlag.SHEAR_ENTITY)
            return
        }

        if (land.hasMemberAccess(event.player.uniqueId)) return
        event.isCancelled = !hasPermission(event.player, LandFlag.SHEAR_ENTITY)
    }

    @EventHandler
    fun handlePlayerLeashEntity(event: PlayerLeashEntityEvent) {
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.entity.chunk)) ?: return
        if (event.entity is Tameable && (event.entity as Tameable).ownerUniqueId == event.player.uniqueId) return
        if (land.hasMemberAccess(event.player.uniqueId)) return
        event.isCancelled = !hasPermission(event.player, LandFlag.ENTITY_LEASH)
    }

    @EventHandler
    fun handlePlayerUnleashEntity(event: PlayerUnleashEntityEvent) {
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.entity.chunk)) ?: return
        if (event.entity is Tameable && (event.entity as Tameable).ownerUniqueId == event.player.uniqueId) return
        if (land.hasMemberAccess(event.player.uniqueId)) return
        event.isCancelled = !hasPermission(event.player, LandFlag.ENTITY_LEASH)
    }
}
