package net.onelitefeather.pandorascluster.listener.player

import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import org.bukkit.Material
import org.bukkit.block.Container
import org.bukkit.block.Jukebox
import org.bukkit.block.data.Powerable
import org.bukkit.block.data.type.Farmland
import org.bukkit.block.data.type.RespawnAnchor
import org.bukkit.block.data.type.TurtleEgg
import org.bukkit.entity.Allay
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.*

class LandPlayerInteractListener(val pandorasClusterApi: PandorasClusterApi) : Listener, EntityUtils, ChunkUtils {

    @EventHandler
    fun handlePlayerHarvestBlock(event: PlayerHarvestBlockEvent) {

        val land = pandorasClusterApi.getLandService().getLand(event.harvestedBlock.chunk.chunkKey)
        if (land == null) {
            event.isCancelled = !hasPermission(event.player, LandFlag.INTERACT_CROPS)
            return
        }

        event.isCancelled = !land.hasMemberAccess(event.player.uniqueId, LandFlag.INTERACT_CROPS)
    }

    @EventHandler
    fun handlePlayerPhysicalInteract(event: PlayerInteractEvent) {

        val player = event.player
        val clickedBlock = event.clickedBlock ?: return
        val land = pandorasClusterApi.getLandService().getLand(clickedBlock.chunk.chunkKey)
        val blockData = clickedBlock.blockData

        event.isCancelled = if (event.action == Action.PHYSICAL) {
            if (blockData is TurtleEgg) {
                cancelTurtleEggDestroy(event.player, land)
            } else if (blockData is Farmland) {
                cancelFarmlandInteract(player, land)
            } else {
                event.useInteractedBlock() == Event.Result.DENY
            }
        } else {
            event.useInteractedBlock() == Event.Result.DENY
        }
    }

    @Suppress("DEPRECATION")
    @EventHandler
    fun handlePlayerUseBoneMeal(event: PlayerInteractEvent) {

        val player = event.player
        val clickedBlock = event.clickedBlock ?: return
        val interactCropsFlag = LandFlag.INTERACT_CROPS

        event.isCancelled = if (event.material == Material.BONE_MEAL) {
            val land = pandorasClusterApi.getLandService().getLand(clickedBlock.chunk.chunkKey)
            if (land != null) {
                if (land.hasMemberAccess(player.uniqueId)) return
                !hasPermission(event.player, interactCropsFlag)
            } else {
                !hasPermission(event.player, interactCropsFlag)
            }
        } else {
            event.isCancelled
        }
    }

    @EventHandler
    fun handlePlayerInteract(event: PlayerInteractEvent) {

        val player = event.player
        val clickedBlock = event.clickedBlock ?: return
        val land = pandorasClusterApi.getLandService().getLand(clickedBlock.chunk.chunkKey)

        val action = event.action
        val blockData = clickedBlock.blockData
        val blockState = clickedBlock.state

        event.isCancelled = if(blockState is Container) {
            cancelContainerInteract(player, land)
        } else if(blockData is RespawnAnchor && action == Action.RIGHT_CLICK_BLOCK) {
            cancelRespawnInteract(player, land, blockData)
        } else if(blockState is Jukebox) {
            cancelJukeboxInteract(player, land)
        } else if(blockData is Powerable) {
            cancelRedstoneInteract(player, land) && event.action == Action.PHYSICAL
        } else {
            event.useInteractedBlock() == Event.Result.DENY
        }
    }

    private fun cancelRedstoneInteract(player: Player, land: Land?): Boolean {

        if (land != null) {
            return !land.hasMemberAccess(player.uniqueId, LandFlag.REDSTONE)
        }

        return !hasPermission(player, LandFlag.REDSTONE)
    }

    private fun cancelJukeboxInteract(player: Player, land: Land?): Boolean {
        if(land == null) return false
        return !land.hasMemberAccess(player.uniqueId, LandFlag.INTERACT_JUKEBOX)
    }

    private fun cancelRespawnInteract(player: Player, land: Land?, respawnAnchor: RespawnAnchor): Boolean {
        val explosionFlag = LandFlag.EXPLOSIONS
        if (land != null) {
            if (land.hasMemberAccess(player.uniqueId, LandFlag.EXPLOSIONS)) return false
            return respawnAnchor.charges == respawnAnchor.maximumCharges
        } else {
            return !hasPermission(player, explosionFlag)
        }
    }

    private fun cancelContainerInteract(player: Player, land: Land?): Boolean {
        if(land == null) return false
        return !land.hasMemberAccess(player.uniqueId, LandFlag.INTERACT_CONTAINERS)
    }

    @EventHandler
    fun handlePlayerEnterBed(event: PlayerBedEnterEvent) {

        val land = pandorasClusterApi.getLandService().getLand(event.bed.chunk.chunkKey)
        val landFlag = LandFlag.USE_BED

        if (land == null) {
            event.isCancelled = !hasPermission(event.player, landFlag)
            return
        }

        event.isCancelled = !land.hasMemberAccess(event.player.uniqueId, LandFlag.USE_BED)
    }

    @EventHandler
    fun handlePlayerTakeLectern(event: PlayerTakeLecternBookEvent) {

        val land = pandorasClusterApi.getLandService().getLand(event.lectern.chunk.chunkKey)
        if (land == null) {
            event.isCancelled = !hasPermission(event.player, LandFlag.TAKE_LECTERN)
            return
        }

        event.isCancelled = !land.hasMemberAccess(event.player.uniqueId)
    }

    private fun cancelFarmlandInteract(player: Player, land: Land?): Boolean {
        return if (land != null) {
            !land.hasMemberAccess(player.uniqueId, LandFlag.INTERACT_CROPS)
        } else {
            !hasPermission(player, LandFlag.INTERACT_CROPS)
        }
    }

    private fun cancelTurtleEggDestroy(player: Player, land: Land?): Boolean {
        return if (land != null) {
            !land.hasMemberAccess(player.uniqueId, LandFlag.TURTLE_EGG_DESTROY)
        } else {
            !hasPermission(player, LandFlag.TURTLE_EGG_DESTROY)
        }
    }

    @EventHandler
    fun handleEntityItemPickup(event: EntityPickupItemEvent) {

        val entity = event.entity
        if(entity is Allay) {
            val item = event.item

            val land = pandorasClusterApi.getLandService().getLand(item.chunk.chunkKey) ?: return
            val thrower = item.thrower ?: return
            event.isCancelled = !land.hasMemberAccess(thrower)
        }
    }
}