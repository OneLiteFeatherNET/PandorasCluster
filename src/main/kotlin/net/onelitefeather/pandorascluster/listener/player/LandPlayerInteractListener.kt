package net.onelitefeather.pandorascluster.listener.player

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import org.bukkit.Material
import org.bukkit.block.Container
import org.bukkit.block.data.type.Farmland
import org.bukkit.block.data.type.RespawnAnchor
import org.bukkit.block.data.type.TurtleEgg
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.*

class LandPlayerInteractListener(val pandorasClusterApi: PandorasClusterApi) : Listener {

    @EventHandler
    fun handlePlayerHarvestBlock(event: PlayerHarvestBlockEvent) {

        val land = pandorasClusterApi.getLand(event.harvestedBlock.chunk)

        if (land == null) {
            event.isCancelled = !Permission.INTERACT_CROPS.hasPermission(event.player)
            return
        }

        if (land.hasAccess(event.player.uniqueId)) return
        if (land.getLandFlag(LandFlag.INTERACT_CROPS).getValue<Boolean>() == true) return
        event.isCancelled = !Permission.INTERACT_CROPS.hasPermission(event.player)
    }

    @EventHandler
    fun handlePlayerPhysicalInteract(event: PlayerInteractEvent) {

        val player = event.player
        val clickedBlock = event.clickedBlock ?: return
        val land = pandorasClusterApi.getLand(clickedBlock.chunk)
        val blockData = clickedBlock.blockData

        event.isCancelled = if (event.action == Action.PHYSICAL) {
            if (blockData is TurtleEgg) {
                if (land != null) {
                    if (land.hasAccess(player.uniqueId)) return
                    if (land.getLandFlag(LandFlag.TURTLE_EGG_DESTROY).getValue<Boolean>() == true) return
                    !Permission.PVE.hasPermission(event.player)
                } else {
                    !Permission.PVE.hasPermission(event.player)
                }
            } else if (blockData is Farmland) {
                if (land != null) {
                    if (land.hasAccess(player.uniqueId)) return
                    if (land.getLandFlag(LandFlag.INTERACT_CROPS).getValue<Boolean>() == true) return
                    !Permission.INTERACT_CROPS.hasPermission(event.player)
                } else {
                    !Permission.INTERACT_CROPS.hasPermission(event.player)
                }
            } else {
                false
            }
        } else {
            false
        }
    }

    @EventHandler
    fun handlePlayerUseBoneMeal(event: PlayerInteractEvent) {

        val player = event.player
        val clickedBlock = event.clickedBlock ?: return

        event.isCancelled = if (event.material == Material.BONE_MEAL) {
            val land = pandorasClusterApi.getLand(clickedBlock.chunk)
            if (land != null) {
                if (land.hasAccess(player.uniqueId)) return
                !player.hasPermission(Permission.FERTILIZE_BLOCK)
            } else {
                !player.hasPermission(Permission.FERTILIZE_BLOCK)
            }
        } else {
            false
        }
    }

    @EventHandler
    fun handlePlayerInteract(event: PlayerInteractEvent) {

        val player = event.player
        val clickedBlock = event.clickedBlock ?: return
        val land = pandorasClusterApi.getLand(clickedBlock.chunk)

        val action = event.action
        val material = clickedBlock.type
        val blockData = clickedBlock.blockData

        event.isCancelled = if (material.isInteractable) {

            if (clickedBlock.state is Container) {
                //Preventing un-trusted for opening containers
                if (land != null) {
                    if (land.hasAccess(player.uniqueId)) return
                    if (land.getLandFlag(LandFlag.INTERACT_CONTAINERS).getValue<Boolean>() == true) return
                    !Permission.INTERACT_CONTAINERS.hasPermission(player)
                } else {
                    !Permission.INTERACT_CONTAINERS.hasPermission(player)
                }
            } else if (blockData is RespawnAnchor && action == Action.RIGHT_CLICK_BLOCK) {
                //Preventing respawnanchors for explode
                if (land != null) {
                    if (land.hasAccess(player.uniqueId)) return
                    if (land.getLandFlag(LandFlag.EXPLOSIONS).getValue<Boolean>() == true) return
                    if (Permission.EXPLOSION.hasPermission(player)) return
                    blockData.charges == blockData.maximumCharges
                } else {
                    !Permission.EXPLOSION.hasPermission(player)
                }
            } else {
                false
            }
        } else {
            false
        }
    }

    @EventHandler
    fun handlePlayerEnterBed(event: PlayerBedEnterEvent) {
        val land = pandorasClusterApi.getLand(event.bed.chunk)
        if (land == null) {
            event.isCancelled = !Permission.ENTER_BED.hasPermission(event.player)
            return
        }

        event.isCancelled = !land.hasAccess(event.player.uniqueId)
    }

    @EventHandler
    fun handlePlayerLeaveBed(event: PlayerBedLeaveEvent) {

        val land = pandorasClusterApi.getLand(event.bed.chunk)
        if (land == null) {
            event.isCancelled = !Permission.LEAVE_BED.hasPermission(event.player)
            return
        }

        event.isCancelled = !land.hasAccess(event.player.uniqueId)
    }

    @EventHandler
    fun handlePlayerTakeLectern(event: PlayerTakeLecternBookEvent) {

        val land = pandorasClusterApi.getLand(event.lectern.chunk)
        if (land == null) {
            event.isCancelled = !Permission.TAKE_LECTERN.hasPermission(event.player)
            return
        }

        event.isCancelled = !land.hasAccess(event.player.uniqueId)
    }
}