package net.onelitefeather.pandorascluster.listener.player

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import org.bukkit.Material
import org.bukkit.block.Container
import org.bukkit.block.Jukebox
import org.bukkit.block.data.Powerable
import org.bukkit.block.data.type.Farmland
import org.bukkit.block.data.type.RespawnAnchor
import org.bukkit.block.data.type.TurtleEgg
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

        val interactCropsFlag = LandFlag.INTERACT_CROPS
        val turtleEggDestroyFlag = LandFlag.TURTLE_EGG_DESTROY

        event.isCancelled = if (event.action == Action.PHYSICAL) {
            if (blockData is TurtleEgg) {
                if (land != null) {
                    if (land.hasAccess(player.uniqueId)) return
                    if (land.getLandFlag(turtleEggDestroyFlag).getValue<Boolean>() == true) return
                    !event.player.hasPermission(turtleEggDestroyFlag)
                } else {
                    !event.player.hasPermission(turtleEggDestroyFlag)
                }
            } else if (blockData is Farmland) {
                if (land != null) {
                    if (land.hasAccess(player.uniqueId)) return
                    if (land.getLandFlag(interactCropsFlag).getValue<Boolean>() == true) return
                    !event.player.hasPermission(interactCropsFlag)
                } else {
                    !event.player.hasPermission(interactCropsFlag)
                }
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
                if (land != null) {
                    if(land.hasAccess(player.uniqueId)) return
                    if(land.isAllowUse(material)) return
                    !player.hasPermission(LandFlag.USE)
                } else {
                    !player.hasPermission(LandFlag.USE)
                }
            } else if (blockData is RespawnAnchor && action == Action.RIGHT_CLICK_BLOCK) {
                val explosionFlag = LandFlag.EXPLOSIONS
                if (land != null) {
                    if (land.hasAccess(player.uniqueId)) return
                    if (land.getLandFlag(explosionFlag).getValue<Boolean>() == true) return
                    if (player.hasPermission(explosionFlag)) return
                    blockData.charges == blockData.maximumCharges
                } else {
                    !player.hasPermission(explosionFlag)
                }
            } else if(blockData is Powerable){
                val redstoneFlag = LandFlag.REDSTONE
                if(land != null) {
                    if(land.hasAccess(player.uniqueId)) return
                    if(land.getLandFlag(redstoneFlag).getValue<Boolean>() == true) return
                    !player.hasPermission(redstoneFlag)
                }
                !player.hasPermission(redstoneFlag)
            } else {
                event.isCancelled
            }
        } else {
            event.isCancelled
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

        event.isCancelled = !land.hasAccess(event.player.uniqueId) || land.getLandFlag(landFlag).getValue<Boolean>() == false
    }

    @EventHandler
    fun handlePlayerLeaveBed(event: PlayerBedLeaveEvent) {

        val land = pandorasClusterApi.getLand(event.bed.chunk)
        val landFlag = LandFlag.USE_BED

        if (land == null) {
            event.isCancelled = !event.player.hasPermission(landFlag)
            return
        }

        event.isCancelled = !land.hasAccess(event.player.uniqueId) || land.getLandFlag(landFlag).getValue<Boolean>() == false
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
}