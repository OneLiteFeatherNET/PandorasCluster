package net.onelitefeather.pandorascluster.listener

import net.onelitefeather.pandorascluster.enums.ChunkRotation
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.service.LandService
import net.onelitefeather.pandorascluster.util.ChunkUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.*

class LandBlockListener(private val landService: LandService) : Listener {

    @EventHandler
    fun handleBlockBreak(event: BlockBreakEvent) {

        val land = landService.getFullLand(event.block.chunk)
        if (land == null) {
            event.isCancelled = !event.player.hasPermission(Permission.BLOCK_BREAK)
            return
        }

        event.isCancelled = !land.hasAccess(event.player.uniqueId)
    }

    @EventHandler
    fun handleBlockPlace(event: BlockPlaceEvent) {

        val hasPermission = event.player.hasPermission(Permission.BLOCK_PLACE)

        val land = landService.getFullLand(event.block.chunk)
        if (land == null) {
            event.isCancelled = !hasPermission
            return
        }

        event.isCancelled = !land.hasAccess(event.player.uniqueId)
    }

    @EventHandler
    fun handleBlockExplode(event: BlockExplodeEvent) {
        val iterator = event.blockList().iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val nextLand = landService.getFullLand(next.chunk)
            if (nextLand != null) {
                val landFlag = landService.getLandFlag(LandFlag.EXPLOSIONS, nextLand)
                if (landFlag != null && !landFlag.getValue<Boolean>()!!) {
                    iterator.remove()
                }
            }
        }
    }

    @EventHandler
    fun handleBlockFromTo(event: BlockFromToEvent) {
        val block = event.block
        val toBlock = event.toBlock
        val blockChunk = landService.getFullLand(block.chunk)
        val toBlockChunk = landService.getFullLand(toBlock.chunk)
        if (blockChunk != null && toBlockChunk != null && !ChunkUtil.hasSameOwner(blockChunk, toBlockChunk)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handleBlockPowered(event: BlockRedstoneEvent) {
        val block = event.block
        val land = landService.getFullLand(block.chunk)
        if (land != null) {
            val landFlag = landService.getLandFlag(LandFlag.REDSTONE, land)
            event.newCurrent = if (landFlag != null && landFlag.getValue()!!) 0 else event.oldCurrent
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handlePistonRetract(event: BlockPistonRetractEvent) {
        val block = event.block
        val location = block.location
        val blockFace = event.direction
        val land = landService.getFullLand(location.chunk)
        if (land != null) {
            for (currentBlock in event.blocks) {
                val location1 = currentBlock.location.add(blockFace.direction)
                val currentLand = landService.getFullLand(location1.chunk)
                if (currentLand != null && !ChunkUtil.hasSameOwner(land, currentLand)) {
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
        val land = landService.getFullLand(location.chunk)
        if (land != null) {
            for (currentBlock in event.blocks) {
                val currentBlockLocation = currentBlock.location.add(blockFace.direction)
                val currentLand = landService.getFullLand(currentBlockLocation.chunk)
                if (currentLand != null && !ChunkUtil.hasSameOwner(land, currentLand)) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handleBlockSpread(event: BlockSpreadEvent) {
        val source = event.source
        val blockState = event.newState
        val sourceChunk = landService.getFullLand(source.chunk)
        val blockStateChunk = landService.getFullLand(blockState.chunk)
        if (sourceChunk != null && blockStateChunk != null) {
            event.isCancelled = !ChunkUtil.hasSameOwner(sourceChunk, blockStateChunk)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun handleBlockGrow(event: BlockGrowEvent) {
        val block = event.block
        val blockState = event.newState
        val land = landService.getFullLand(block.chunk)
        if (land != null) {
            val blockFace = ChunkRotation.getBlockFace(block.location)
            val faceLocation = blockState.location.subtract(blockFace.direction)
            val blockFaceLand = landService.getFullLand(faceLocation.chunk)
            if (blockFaceLand != null && !ChunkUtil.hasSameOwner(blockFaceLand, land)) {
                event.isCancelled = true
            }
        }
    }
}