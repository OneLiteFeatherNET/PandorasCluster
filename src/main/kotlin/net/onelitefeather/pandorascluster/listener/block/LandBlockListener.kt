package net.onelitefeather.pandorascluster.listener.block

import io.papermc.paper.event.block.PlayerShearBlockEvent
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.util.getBlockFace
import net.onelitefeather.pandorascluster.util.hasSameOwner
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

class LandBlockListener(private val pandorasClusterApi: PandorasClusterApi) : Listener, EntityUtils {

    @EventHandler
    fun handleSpongeAbsorb(event: SpongeAbsorbEvent) {
        event.blocks.groupBy(BlockState::getChunk).filter(this::filterSpongeAbsorb).forEach { event.blocks.removeAll(it.value) }
    }

    @EventHandler
    fun handleBlockIgnite(event: BlockIgniteEvent) {

        val block = event.block
        val ignitedBlock = event.ignitingBlock

        val land = pandorasClusterApi.getLand(ignitedBlock?.chunk ?: block.chunk) ?: return
        val landFlag = land.getLandFlag(LandFlag.FIRE_PROTECTION)

        val ignitingEntity = event.ignitingEntity
        event.isCancelled = if(ignitingEntity != null) {
            !land.hasAccess(ignitingEntity.uniqueId)
        } else {
            landFlag.getValue<Boolean>() == false
        }
    }

    @EventHandler
    fun handleBlockBurn(event: BlockBurnEvent) {

        val block = event.block
        val ignitedBlock = event.ignitingBlock

        val land = pandorasClusterApi.getLand(ignitedBlock?.chunk ?: block.chunk) ?: return
        val landFlag = land.getLandFlag(LandFlag.FIRE_PROTECTION)
        event.isCancelled = landFlag.getValue<Boolean>() == false
    }

    @EventHandler
    fun handleBlockBreak(event: BlockBreakEvent) {

        if (hasPermission(event.player, Permission.BLOCK_BREAK)) return
        val land = pandorasClusterApi.getLand(event.block.chunk)
        if (land == null) {
            event.isCancelled = true
            return
        }

        event.isCancelled = !land.hasAccess(event.player.uniqueId)
    }

    @EventHandler
    fun handleBlockPlace(event: BlockPlaceEvent) {

        if (hasPermission(event.player, Permission.BLOCK_PLACE)) return
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
        if (event is EntityBlockFormEvent) return
        event.isCancelled = land.getLandFlag(LandFlag.BLOCK_FORM).getValue<Boolean>() == false
    }

    @EventHandler
    fun handleBlockFromTo(event: BlockFromToEvent) {

        val blockChunk = event.block.chunk
        val toBlockChunk = event.toBlock.chunk
        event.isCancelled = if (blockChunk != toBlockChunk) {
            val land = pandorasClusterApi.getLand(event.toBlock.chunk)
            val toLand = pandorasClusterApi.getLand(event.block.chunk)
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
        val block = event.block
        val land = pandorasClusterApi.getLand(block.chunk)
        if (land != null) {
            val landFlag = land.getLandFlag(LandFlag.REDSTONE)
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

    @EventHandler
    fun handlePlayerBlockShear(event: PlayerShearBlockEvent) {
        val land = pandorasClusterApi.getLand(event.block.chunk)
        event.isCancelled = if (land != null) {
            if (land.hasAccess(event.player.uniqueId)) return
            !hasPermission(event.player, LandFlag.SHEAR_BLOCK)
        } else {
            !hasPermission(event.player, LandFlag.SHEAR_BLOCK)
        }
    }

    @EventHandler
    fun handlePlayerBucketUse(event: PlayerBucketFillEvent) {
        val land = pandorasClusterApi.getLand(event.blockClicked.chunk)
        event.isCancelled = if (land != null) {
            if (land.hasAccess(event.player.uniqueId)) return
            !hasPermission(event.player, LandFlag.BUCKET_INTERACT)
        } else {
            !hasPermission(event.player, LandFlag.BUCKET_INTERACT)
        }
    }

    @EventHandler
    fun handlePlayerBucketUse(event: PlayerBucketEmptyEvent) {
        val land = pandorasClusterApi.getLand(event.blockClicked.chunk)
        event.isCancelled = if (land != null) {
            if (land.hasAccess(event.player.uniqueId)) return
            !hasPermission(event.player, LandFlag.BUCKET_INTERACT)
        } else {
            !hasPermission(event.player, LandFlag.BUCKET_INTERACT)
        }
    }

    @EventHandler
    fun handleEntityExplode(event: TNTPrimeEvent) {
        val block = event.primingBlock ?: event.block
        val land = pandorasClusterApi.getLand(block.chunk)
        val primerEntity = event.primingEntity
        if (land != null) {

            val landFlag = land.getLandFlag(LandFlag.EXPLOSIONS)
            val explosionsDisallowed = landFlag.getValue<Boolean>() == false
            val hasNoAccessAndNoPermissions = { entity: Entity ->
                !land.hasAccess(entity.uniqueId) && !hasPermission(entity, LandFlag.EXPLOSIONS)
            }

            event.isCancelled = if (explosionsDisallowed) {
                true
            } else if (primerEntity != null) {
                hasNoAccessAndNoPermissions(primerEntity)
            } else false
        }
    }


    @EventHandler
    fun handleBlockExplode(event: BlockExplodeEvent) {
        event.blockList().groupBy(Block::getChunk).filter(this::filterForNoExplosiveLands).forEach { event.blockList().removeAll(it.value) }
    }

    private fun filterForNoExplosiveLands(land: Map.Entry<Chunk, List<Block>>): Boolean {
        return pandorasClusterApi.getLand(land.key)?.getLandFlag(LandFlag.EXPLOSIONS)?.getValue<Boolean>() == false
    }

    private fun filterSpongeAbsorb(land: Map.Entry<Chunk, List<BlockState>>): Boolean {
        return pandorasClusterApi.getLand(land.key)?.getLandFlag(LandFlag.SPONGE_ABSORB)?.getValue<Boolean>() == false
    }
}