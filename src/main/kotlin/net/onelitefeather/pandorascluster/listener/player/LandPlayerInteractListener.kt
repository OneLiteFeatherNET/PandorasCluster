package net.onelitefeather.pandorascluster.listener.player

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Container
import org.bukkit.block.Jukebox
import org.bukkit.block.data.Powerable
import org.bukkit.block.data.type.Farmland
import org.bukkit.block.data.type.RespawnAnchor
import org.bukkit.block.data.type.TurtleEgg
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.*

@Suppress("kotlin:S1874")
class LandPlayerInteractListener(val pandorasClusterApi: PandorasClusterApi) : Listener {

    @EventHandler
    fun handlePlayerHarvestBlock(event: PlayerHarvestBlockEvent) {

        val land = pandorasClusterApi.getLand(event.harvestedBlock.chunk)
        val landFlag = LandFlag.INTERACT_CROPS

        if (land == null) {
            event.isCancelled = !event.player.hasPermission(landFlag)
            return
        }

        if (land.hasAccess(event.player.uniqueId)) return
        if (land.getLandFlag(landFlag).getValue<Boolean>() == true) return
        event.isCancelled = !event.player.hasPermission(landFlag)
    }

    @Suppress("DEPRECATION")
    @EventHandler
    fun handlePlayerPhysicalInteract(event: PlayerInteractEvent) {

        val player = event.player
        val clickedBlock = event.clickedBlock ?: return
        val land = pandorasClusterApi.getLand(clickedBlock.chunk)
        val blockData = clickedBlock.blockData

        event.isCancelled = if (event.action == Action.PHYSICAL) {
            if (blockData is TurtleEgg) {
                cancelTurtleEggDestroy(event.player, land)
            } else if (blockData is Farmland) {
                cancelFarmlandInteract(player, land)
            } else {
                event.isCancelled
            }
        } else {
            event.isCancelled
        }
    }

    @Suppress("DEPRECATION")
    @EventHandler
    fun handlePlayerUseBoneMeal(event: PlayerInteractEvent) {

        val player = event.player
        val clickedBlock = event.clickedBlock ?: return
        val interactCropsFlag = LandFlag.INTERACT_CROPS

        event.isCancelled = if (event.material == Material.BONE_MEAL) {
            val land = pandorasClusterApi.getLand(clickedBlock.chunk)
            if (land != null) {
                if (land.hasAccess(player.uniqueId)) return
                !event.player.hasPermission(interactCropsFlag)
            } else {
                !event.player.hasPermission(interactCropsFlag)
            }
        } else {
            event.isCancelled
        }
    }

    @Suppress("DEPRECATION")
    @EventHandler
    fun handlePlayerInteract(event: PlayerInteractEvent) {

        val player = event.player
        val clickedBlock = event.clickedBlock ?: return
        val land = pandorasClusterApi.getLand(clickedBlock.chunk)

        val action = event.action
        val material = clickedBlock.type
        val blockData = clickedBlock.blockData
        val blockState = clickedBlock.state

        event.isCancelled = if (material.isInteractable) {
            if (blockState is Container || blockState is Jukebox) {
                cancelBlockUse(player, clickedBlock, land)
            } else if (blockData is RespawnAnchor && action == Action.RIGHT_CLICK_BLOCK) {
                cancelRespawnInteract(player, land, blockData)
            } else if (blockData is Powerable) {
                cancelRedstoneInteract(player, land)
            } else {
                event.isCancelled
            }
        } else {
            event.isCancelled
        }
    }

    private fun cancelRedstoneInteract(player: Player, land: Land?): Boolean {

        val redstoneFlag = LandFlag.REDSTONE
        if (land != null) {
            if (land.hasAccess(player.uniqueId)) return false
            if (land.getLandFlag(redstoneFlag).getValue<Boolean>() == true) return false
            return !player.hasPermission(redstoneFlag)
        }

        return !player.hasPermission(redstoneFlag)
    }

    private fun cancelRespawnInteract(player: Player, land: Land?, respawnAnchor: RespawnAnchor): Boolean {
        val explosionFlag = LandFlag.EXPLOSIONS
        if (land != null) {
            if (land.hasAccess(player.uniqueId)) return false
            if (land.getLandFlag(explosionFlag).getValue<Boolean>() == true) return false
            if (player.hasPermission(explosionFlag)) return false
            return respawnAnchor.charges == respawnAnchor.maximumCharges
        } else {
            return !player.hasPermission(explosionFlag)
        }
    }

    private fun cancelBlockUse(player: Player, block: Block, land: Land?): Boolean {
        if (land != null) {
            if (land.hasAccess(player.uniqueId)) return false
            if (land.isAllowUse(block.type)) return false
            return !player.hasPermission(LandFlag.USE)
        } else {
            return !player.hasPermission(LandFlag.USE)
        }
    }

    @EventHandler
    fun handlePlayerEnterBed(event: PlayerBedEnterEvent) {

        val land = pandorasClusterApi.getLand(event.bed.chunk)
        val landFlag = LandFlag.USE_BED

        if (land == null) {
            event.isCancelled = !event.player.hasPermission(landFlag)
            return
        }

        event.isCancelled =
            !land.hasAccess(event.player.uniqueId) || land.getLandFlag(landFlag).getValue<Boolean>() == false
    }

    @EventHandler
    fun handlePlayerLeaveBed(event: PlayerBedLeaveEvent) {

        val land = pandorasClusterApi.getLand(event.bed.chunk)
        val landFlag = LandFlag.USE_BED

        if (land == null) {
            event.isCancelled = !event.player.hasPermission(landFlag)
            return
        }

        event.isCancelled =
            !land.hasAccess(event.player.uniqueId) || land.getLandFlag(landFlag).getValue<Boolean>() == false
    }

    @EventHandler
    fun handlePlayerTakeLectern(event: PlayerTakeLecternBookEvent) {

        val land = pandorasClusterApi.getLand(event.lectern.chunk)
        if (land == null) {
            event.isCancelled = !event.player.hasPermission(LandFlag.TAKE_LECTERN)
            return
        }

        event.isCancelled = !land.hasAccess(event.player.uniqueId)
    }

    private fun cancelFarmlandInteract(player: Player, land: Land?): Boolean {
        if (land != null) {
            if (land.hasAccess(player.uniqueId)) return false
            if (land.getLandFlag(LandFlag.INTERACT_CROPS).getValue<Boolean>() == true) return false
            return !player.hasPermission(LandFlag.INTERACT_CROPS)
        } else {
            return !player.hasPermission(LandFlag.INTERACT_CROPS)
        }
    }

    private fun cancelTurtleEggDestroy(player: Player, land: Land?): Boolean {
        if (land != null) {
            if (land.hasAccess(player.uniqueId)) return false
            if (land.getLandFlag(LandFlag.TURTLE_EGG_DESTROY).getValue<Boolean>() == true) return false
            return !player.hasPermission(LandFlag.TURTLE_EGG_DESTROY)
        } else {
            return !player.hasPermission(LandFlag.TURTLE_EGG_DESTROY)
        }
    }
}