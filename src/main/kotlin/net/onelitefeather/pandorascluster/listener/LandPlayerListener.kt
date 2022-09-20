package net.onelitefeather.pandorascluster.listener

import io.papermc.paper.event.block.PlayerShearBlockEvent
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import org.bukkit.block.Container
import org.bukkit.block.data.type.Farmland
import org.bukkit.block.data.type.RespawnAnchor
import org.bukkit.block.data.type.TurtleEgg
import org.bukkit.entity.Tameable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.player.*
import org.spigotmc.event.player.PlayerSpawnLocationEvent

class LandPlayerListener(private val pandorasClusterApi: PandorasClusterApi) :
    Listener {

    @EventHandler
    fun handlePlayerMovement(event: PlayerMoveEvent) {
        val player = event.player
        if (!event.hasExplicitlyChangedBlock()) return
        val toLand = pandorasClusterApi.getLand(event.to.chunk)
        if (toLand != null) {
            if (Permission.LAND_ENTRY_DENIED.hasPermission(player)) return
            if (!toLand.isBanned(player.uniqueId)) return
            event.to = event.from
        }
    }

    @EventHandler
    fun handlePlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        val to = event.to
        val from = event.from
        val land = pandorasClusterApi.getLand(to.chunk) ?: pandorasClusterApi.getLand(from.chunk) ?: return
        if (Permission.LAND_ENTRY_DENIED.hasPermission(player)) return
        if (land.isBanned(player.uniqueId)) {
            event.to = event.from
        }
    }

    @EventHandler
    fun handlePlayerRespawn(event: PlayerRespawnEvent) {
        val land = pandorasClusterApi.getLand(event.respawnLocation.chunk)
        if (Permission.LAND_ENTRY_DENIED.hasPermission(event.player)) return
        if (land == null || !land.isBanned(event.player.uniqueId)) return
        event.respawnLocation = event.respawnLocation.world.spawnLocation.toCenterLocation()
    }

    @EventHandler
    fun handlePlayerSpawn(event: PlayerSpawnLocationEvent) {
        val land = pandorasClusterApi.getLand(event.spawnLocation.chunk)
        if (Permission.LAND_ENTRY_DENIED.hasPermission(event.player)) return
        if (land == null || !land.isBanned(event.player.uniqueId)) return
        event.spawnLocation = event.spawnLocation.world.spawnLocation.toCenterLocation()
    }

    @EventHandler
    fun handlePlayerHarvestBlock(event: PlayerHarvestBlockEvent) {
        val land = pandorasClusterApi.getLand(event.harvestedBlock.chunk)
        if (Permission.INTERACT_FARMLAND.hasPermission(event.player)) return
        if (land == null || !land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }

    @Suppress("kotlin:S3776")
    @EventHandler
    fun handlePlayerInteract(event: PlayerInteractEvent) {

        val clickedBlock = event.clickedBlock ?: return
        val land = pandorasClusterApi.getLand(clickedBlock.chunk) ?: return

        if (land.hasAccess(event.player.uniqueId)) return
        val blockData = clickedBlock.blockData
        event.isCancelled = if (event.material.isInteractable) {
            if (Permission.INTERACT_USE.hasPermission(event.player)) return
            true
        } else if (blockData is Farmland && event.action == Action.PHYSICAL) {
            if (pandorasClusterApi.getLandFlag(LandFlag.FARMLAND_DESTROY, land)?.getValue<Boolean>() == true) return
            if (Permission.INTERACT_FARMLAND.hasPermission(event.player)) return
            true
        } else if (clickedBlock.state is Container) {
            if (Permission.INTERACT_CONTAINERS.hasPermission(event.player)) return
            true
        } else if (blockData is RespawnAnchor &&
            event.action == Action.RIGHT_CLICK_BLOCK &&
            blockData.charges == blockData.maximumCharges
        ) {
            val landFlag = pandorasClusterApi.getLandFlag(LandFlag.EXPLOSIONS, land) ?: return
            if (landFlag.getValue<Boolean>() == true) return
            if (Permission.EXPLOSION.hasPermission(event.player)) return
            true
        } else if(blockData is TurtleEgg) {
            !Permission.PVE.hasPermission(event.player)
        } else {
            false
        }
    }

    @EventHandler
    fun handlePlayerBucketUse(event: PlayerBucketFillEvent) {
        val land = pandorasClusterApi.getLand(event.blockClicked.chunk)
        if (Permission.BUCKET_USE.hasPermission(event.player)) return
        if (land == null || !land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handlePlayerBucketUse(event: PlayerBucketEmptyEvent) {
        val land = pandorasClusterApi.getLand(event.blockClicked.chunk)
        if (Permission.BUCKET_USE.hasPermission(event.player)) return
        if (land == null || !land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handlePlayerBucketUse(event: PlayerBucketEntityEvent) {
        val land = pandorasClusterApi.getLand(event.entity.chunk)
        if (Permission.BUCKET_USE.hasPermission(event.player)) return
        if (land == null || !land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handlePlayerEntityShear(event: PlayerShearEntityEvent) {
        val land = pandorasClusterApi.getLand(event.entity.chunk)
        if (Permission.SHEAR_ENTITY.hasPermission(event.player)) return
        if (land == null || !land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handlePlayerBlockShear(event: PlayerShearBlockEvent) {
        val land = pandorasClusterApi.getLand(event.block.chunk)
        if (Permission.SHEAR_BLOCK.hasPermission(event.player)) return
        if (land == null || !land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handlePlayerEnterBed(event: PlayerBedEnterEvent) {
        val land = pandorasClusterApi.getLand(event.bed.chunk)
        if (Permission.ENTER_BED.hasPermission(event.player)) return
        if (land == null || !land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handlePlayerLeaveBed(event: PlayerBedLeaveEvent) {
        val land = pandorasClusterApi.getLand(event.bed.chunk)
        if (Permission.LEAVE_BED.hasPermission(event.player)) return
        if (land == null || !land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handlePlayerTakeLectern(event: PlayerTakeLecternBookEvent) {
        val land = pandorasClusterApi.getLand(event.lectern.chunk)
        if (Permission.TAKE_LECTERN.hasPermission(event.player)) return
        if (land == null || !land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handlePlayerLeashEntity(event: PlayerLeashEntityEvent) {
        val land = pandorasClusterApi.getLand(event.entity.chunk)
        if (Permission.LEASH_ENTITY.hasPermission(event.player)) return
        if (event.entity is Tameable && (event.entity as Tameable).ownerUniqueId == event.player.uniqueId) return
        if (land == null || !land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handlePlayerUnleashEntity(event: PlayerUnleashEntityEvent) {
        val land = pandorasClusterApi.getLand(event.entity.chunk)
        if (Permission.UNLEASH_ENTITY.hasPermission(event.player)) return
        if (event.entity is Tameable && (event.entity as Tameable).ownerUniqueId == event.player.uniqueId) return
        if (land == null || !land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }

}