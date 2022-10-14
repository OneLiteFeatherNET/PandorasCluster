package net.onelitefeather.pandorascluster.listener

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.util.getBlockFace
import net.onelitefeather.pandorascluster.util.hasSameOwner
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.*

class LandBlockListener(private val pandorasClusterApi: PandorasClusterApi) : Listener {

    @EventHandler
    fun handleBlockBreak(event: BlockBreakEvent) {

        if (event.player.hasPermission(Permission.BLOCK_BREAK)) return
        val land = pandorasClusterApi.getLand(event.block.chunk)
        if (land == null) {
            event.isCancelled = true
            return
        }

        event.isCancelled = !land.hasAccess(event.player.uniqueId)
    }

    @EventHandler
    fun handleBlockPlace(event: BlockPlaceEvent) {

        if (event.player.hasPermission(Permission.BLOCK_PLACE)) return
        val land = pandorasClusterApi.getLand(event.block.chunk)
        if (land == null) {
            event.isCancelled = true
            return
        }

        event.isCancelled = !land.hasAccess(event.player.uniqueId)
    }

    @EventHandler
    fun handleBlockForm(event: BlockFormEvent) {
        val land = pandorasClusterApi.getLand(event.block.chunk) ?: return
        event.isCancelled = land.getLandFlag(LandFlag.BLOCK_FORM).getValue<Boolean>() == false

    }

    @EventHandler
    fun handleEntityBlockForm(event: EntityBlockFormEvent) {
        val land = pandorasClusterApi.getLand(event.block.chunk) ?: return
        event.isCancelled = land.getLandFlag(LandFlag.BLOCK_FORM).getValue<Boolean>() == false
    }

    @EventHandler
    fun handleBlockFromTo(event: BlockFromToEvent) {
        val block = event.block
        val toBlock = event.toBlock
        val blockChunk = pandorasClusterApi.getLand(block.chunk)
        val toBlockChunk = pandorasClusterApi.getLand(toBlock.chunk)
        if (blockChunk != null && toBlockChunk != null && !hasSameOwner(blockChunk, toBlockChunk)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handleBlockPowered(event: BlockRedstoneEvent) {
        val block = event.block
        val land = pandorasClusterApi.getLand(block.chunk)
        if (land != null) {
            val landFlag = land.getLandFlag(LandFlag.REDSTONE) ?: return
            if (landFlag.getValue<Boolean>() == false) {
                event.newCurrent = 0
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handlePistonRetract(event: BlockPistonRetractEvent) {
        val block = event.block
        val location = block.location
        val blockFace = event.direction
        val land = pandorasClusterApi.getLand(location.chunk)
        if (land != null) {
            for (currentBlock in event.blocks) {
                val location1 = currentBlock.location.add(blockFace.direction)
                val currentLand = pandorasClusterApi.getLand(location1.chunk)
                if (currentLand != null && !hasSameOwner(land, currentLand)) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handlePistonExtend(event: BlockPistonExtendEvent) {
        val block = event.block
        val location = block.location
        val blockFace = event.direction
        val land = pandorasClusterApi.getLand(location.chunk)
        if (land != null) {
            for (currentBlock in event.blocks) {
                val currentBlockLocation = currentBlock.location.add(blockFace.direction)
                val currentLand = pandorasClusterApi.getLand(currentBlockLocation.chunk)
                if (currentLand != null && !hasSameOwner(land, currentLand)) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handleBlockSpread(event: BlockSpreadEvent) {

        val sourceChunk = pandorasClusterApi.getLand(event.source.chunk)
        val blockStateChunk = pandorasClusterApi.getLand(event.newState.chunk)
        val cancel = if (sourceChunk == null && blockStateChunk != null) {
            true
        } else if (sourceChunk != null && blockStateChunk == null) {
            true
        } else if (sourceChunk != null && blockStateChunk != null) {
            !hasSameOwner(sourceChunk, blockStateChunk)
        } else {
            false
        }

        event.isCancelled = cancel
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun handleBlockGrow(event: BlockGrowEvent) {
        val block = event.block
        val blockState = event.newState
        val land = pandorasClusterApi.getLand(block.chunk)
        if (land != null) {
            val blockFace = getBlockFace(block.location)
            val faceLocation = blockState.location.subtract(blockFace?.direction ?: BlockFace.SELF.direction)
            val blockFaceLand = pandorasClusterApi.getLand(faceLocation.chunk)
            if (blockFaceLand != null && !hasSameOwner(blockFaceLand, land)) {
                event.isCancelled = true
            }
        }
    }
}