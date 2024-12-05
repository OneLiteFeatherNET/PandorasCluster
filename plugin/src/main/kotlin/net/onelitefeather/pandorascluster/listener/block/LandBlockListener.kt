package net.onelitefeather.pandorascluster.listener.block

import io.papermc.paper.event.block.PlayerShearBlockEvent
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.enums.Permission
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import org.bukkit.Chunk
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.BlockState
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent

class LandBlockListener(private val pandorasClusterApi: PandorasClusterApi) : Listener, EntityUtils, ChunkUtils {

    @EventHandler
    fun handleSpongeAbsorb(event: SpongeAbsorbEvent) {
        event.blocks.groupBy(BlockState::getChunk).filter(this::filterSpongeAbsorb)
            .forEach { event.blocks.removeAll(it.value) }
    }

    @EventHandler
    fun handleBlockIgnite(event: BlockIgniteEvent) {

        val block = event.block
        val ignitedBlock = event.ignitingBlock

        val chunk = ignitedBlock?.chunk?.chunkKey ?: block.chunk.chunkKey

        val land = pandorasClusterApi.getLandService().getLand(chunk) ?: return

        val ignitingEntity = event.ignitingEntity
        event.isCancelled = if (ignitingEntity != null) {
            !land.hasMemberAccess(ignitingEntity.uniqueId, LandFlag.FIRE_PROTECTION)
        } else {
            !land.hasFlag(LandFlag.FIRE_PROTECTION)
        }
    }

    @EventHandler
    fun handleBlockBurn(event: BlockBurnEvent) {

        val block = event.block
        val ignitedBlock = event.ignitingBlock

        val chunk = ignitedBlock?.chunk ?: block.chunk

        val land = pandorasClusterApi.getLandService().getLand(chunk.chunkKey) ?: return
        event.isCancelled = !land.hasFlag(LandFlag.FIRE_PROTECTION)
    }

    @EventHandler
    fun handleBlockBreak(event: BlockBreakEvent) {
        val land = pandorasClusterApi.getLandService().getLand(event.block.chunk.chunkKey)
        if (land == null) {
            event.isCancelled = !hasPermission(event.player, Permission.BLOCK_BREAK)
            return
        }

        event.isCancelled = !land.hasMemberAccess(event.player.uniqueId, LandFlag.BLOCK_BREAK)
    }

    @EventHandler
    fun handleBlockPlace(event: BlockPlaceEvent) {

        val land = pandorasClusterApi.getLandService().getLand(event.block.chunk.chunkKey)
        if (land == null) {
            event.isCancelled = !hasPermission(event.player, Permission.BLOCK_PLACE)
            return
        }

        event.isCancelled = !land.hasMemberAccess(event.player.uniqueId, LandFlag.BLOCK_PLACE)
    }

    @EventHandler
    fun handleBlockForm(event: BlockFormEvent) {
        val land = pandorasClusterApi.getLandService().getLand(event.block.chunk.chunkKey) ?: return
        if (event is EntityBlockFormEvent) return
        event.isCancelled = !land.hasFlag(LandFlag.BLOCK_FORM)
    }

    @EventHandler
    fun handleBlockFromTo(event: BlockFromToEvent) {

        val blockChunk = event.block.chunk
        val toBlockChunk = event.toBlock.chunk
        event.isCancelled = if (blockChunk != toBlockChunk) {
            val land = pandorasClusterApi.getLandService().getLand(event.toBlock.chunk.chunkKey)
            val toLand = pandorasClusterApi.getLandService().getLand(event.block.chunk.chunkKey)
            if (toLand != null && land == null || land != null && toLand == null) {
                true
            } else {
                toLand != null && land != null && !hasSameOwner(land, toLand)
            }
        } else {
            false
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handleBlockPowered(event: BlockRedstoneEvent) {
        val land = pandorasClusterApi.getLandService().getLand(event.block.chunk.chunkKey)
        if (land != null) {
            event.newCurrent = if (land.hasFlag(LandFlag.REDSTONE)) event.newCurrent else event.oldCurrent
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handlePistonRetract(event: BlockPistonRetractEvent) {
        val block = event.block
        val location = block.location
        val blockFace = event.direction
        val land = pandorasClusterApi.getLandService().getLand(location.chunk.chunkKey)
        if (land != null) {
            for (currentBlock in event.blocks) {
                val location1 = currentBlock.location.add(blockFace.direction)
                val currentLand = pandorasClusterApi.getLandService().getLand(location1.chunk.chunkKey)
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
        val land = pandorasClusterApi.getLandService().getLand(location.chunk.chunkKey)
        if (land != null) {
            for (currentBlock in event.blocks) {
                val currentBlockLocation = currentBlock.location.add(blockFace.direction)
                val currentLand = pandorasClusterApi.getLandService().getLand(currentBlockLocation.chunk.chunkKey)
                if (currentLand != null && !hasSameOwner(land, currentLand)) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handleBlockSpread(event: BlockSpreadEvent) {

        val sourceChunk = pandorasClusterApi.getLandService().getLand(event.source.chunk.chunkKey)
        val blockStateChunk = pandorasClusterApi.getLandService().getLand(event.newState.chunk.chunkKey)
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
        val land = pandorasClusterApi.getLandService().getLand(block.chunk.chunkKey)
        if (land != null) {
            val blockFace = getBlockFace(block.location)
            val faceLocation = blockState.location.subtract(blockFace?.direction ?: BlockFace.SELF.direction)
            val blockFaceLand = pandorasClusterApi.getLandService().getLand(faceLocation.chunk.chunkKey)
            if (blockFaceLand != null && !hasSameOwner(blockFaceLand, land)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun handlePlayerBlockShear(event: PlayerShearBlockEvent) {
        val land = pandorasClusterApi.getLandService().getLand(event.block.chunk.chunkKey)
        event.isCancelled = if (land != null) {
            !land.hasMemberAccess(event.player.uniqueId, LandFlag.SHEAR_BLOCK)
        } else {
            !hasPermission(event.player, LandFlag.SHEAR_BLOCK)
        }
    }

    @EventHandler
    fun handlePlayerBucketUse(event: PlayerBucketFillEvent) {
        val land = pandorasClusterApi.getLandService().getLand(event.blockClicked.chunk.chunkKey)
        event.isCancelled = if (land != null) {
            !land.hasMemberAccess(event.player.uniqueId, LandFlag.BUCKET_INTERACT)
        } else {
            !hasPermission(event.player, LandFlag.BUCKET_INTERACT)
        }
    }

    @EventHandler
    fun handlePlayerBucketUse(event: PlayerBucketEmptyEvent) {
        val land = pandorasClusterApi.getLandService().getLand(event.blockClicked.chunk.chunkKey)
        event.isCancelled = if (land != null) {
            !land.hasMemberAccess(event.player.uniqueId, LandFlag.BUCKET_INTERACT)
        } else {
            !hasPermission(event.player, LandFlag.BUCKET_INTERACT)
        }
    }

    @EventHandler
    fun handleEntityExplode(event: TNTPrimeEvent) {
        val block = event.primingBlock ?: event.block
        val land = pandorasClusterApi.getLandService().getLand(block.chunk.chunkKey)
        val primerEntity = event.primingEntity

        if (land == null) {
            event.isCancelled = true
            return
        }

        val explosionsDisallowed = !land.hasFlag(LandFlag.EXPLOSIONS)
        val playerExplosionsDisabled = { entity: Entity ->
            !land.hasMemberAccess(entity.uniqueId, LandFlag.EXPLOSIONS)
        }

        event.isCancelled = if (explosionsDisallowed) {
            true
        } else if (primerEntity != null) {
            playerExplosionsDisabled(primerEntity)
        } else false
    }

    @EventHandler
    fun handleBlockExplode(event: BlockExplodeEvent) {
        event.blockList().groupBy(Block::getChunk).filter(this::filterForNoExplosiveLands)
            .forEach { event.blockList().removeAll(it.value) }
    }

    private fun filterForNoExplosiveLands(landEntry: Map.Entry<Chunk, List<Block>>): Boolean {
        val land = pandorasClusterApi.getLandService().getLand(landEntry.key.chunkKey) ?: return true
        return !land.hasFlag(LandFlag.EXPLOSIONS)
    }

    private fun filterSpongeAbsorb(landEntry: Map.Entry<Chunk, List<BlockState>>): Boolean {
        val land = pandorasClusterApi.getLandService().getLand(landEntry.key.chunkKey) ?: return true
        return !land.hasFlag(LandFlag.EXPLOSIONS)
    }
}