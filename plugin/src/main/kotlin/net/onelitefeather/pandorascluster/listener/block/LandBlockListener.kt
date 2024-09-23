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

        val land =
            pandorasClusterApi.getLandService().getLand(toClaimedChunk(ignitedBlock?.chunk ?: block.chunk)) ?: return
        val landFlag = land.getFlag(LandFlag.FIRE_PROTECTION)

        val ignitingEntity = event.ignitingEntity
        event.isCancelled = if (ignitingEntity != null) {
            !land.hasMemberAccess(ignitingEntity.uniqueId)
        } else {
            landFlag.getValue<Boolean>() == false
        }
    }

    @EventHandler
    fun handleBlockBurn(event: BlockBurnEvent) {

        val block = event.block
        val ignitedBlock = event.ignitingBlock

        val land =
            pandorasClusterApi.getLandService().getLand(toClaimedChunk(ignitedBlock?.chunk ?: block.chunk)) ?: return
        val landFlag = land.getFlag(LandFlag.FIRE_PROTECTION)
        event.isCancelled = landFlag.getValue<Boolean>() == false
    }

    @EventHandler
    fun handleBlockBreak(event: BlockBreakEvent) {

        if (hasPermission(event.player, Permission.BLOCK_BREAK)) return
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.block.chunk))
        if (land == null) {
            event.isCancelled = true
            return
        }

        event.isCancelled = !land.hasMemberAccess(event.player.uniqueId)
    }

    @EventHandler
    fun handleBlockPlace(event: BlockPlaceEvent) {

        if (hasPermission(event.player, Permission.BLOCK_PLACE)) return
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.block.chunk))
        if (land == null) {
            event.isCancelled = true
            return
        }

        event.isCancelled = !land.hasMemberAccess(event.player.uniqueId)
    }

    @EventHandler
    fun handleBlockForm(event: BlockFormEvent) {
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.block.chunk)) ?: return
        if (event is EntityBlockFormEvent) return
        event.isCancelled = land.getFlag(LandFlag.BLOCK_FORM).getValue<Boolean>() == false
    }

    @EventHandler
    fun handleBlockFromTo(event: BlockFromToEvent) {

        val blockChunk = event.block.chunk
        val toBlockChunk = event.toBlock.chunk
        event.isCancelled = if (blockChunk != toBlockChunk) {
            val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.toBlock.chunk))
            val toLand = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.block.chunk))
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
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.block.chunk))
        if (land != null) {
            val landFlag = land.getFlag(LandFlag.REDSTONE)
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
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(location.chunk))
        if (land != null) {
            for (currentBlock in event.blocks) {
                val location1 = currentBlock.location.add(blockFace.direction)
                val currentLand = pandorasClusterApi.getLandService().getLand(toClaimedChunk(location1.chunk))
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
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(location.chunk))
        if (land != null) {
            for (currentBlock in event.blocks) {
                val currentBlockLocation = currentBlock.location.add(blockFace.direction)
                val currentLand = pandorasClusterApi.getLandService().getLand(toClaimedChunk(currentBlockLocation.chunk))
                if (currentLand != null && !hasSameOwner(land, currentLand)) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handleBlockSpread(event: BlockSpreadEvent) {

        val sourceChunk = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.source.chunk))
        val blockStateChunk = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.newState.chunk))
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
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(block.chunk))
        if (land != null) {
            val blockFace = getBlockFace(block.location)
            val faceLocation = blockState.location.subtract(blockFace?.direction ?: BlockFace.SELF.direction)
            val blockFaceLand = pandorasClusterApi.getLandService().getLand(toClaimedChunk(faceLocation.chunk))
            if (blockFaceLand != null && !hasSameOwner(blockFaceLand, land)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun handlePlayerBlockShear(event: PlayerShearBlockEvent) {
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.block.chunk))
        event.isCancelled = if (land != null) {
            if (land.hasMemberAccess(event.player.uniqueId)) return
            !hasPermission(event.player, LandFlag.SHEAR_BLOCK)
        } else {
            !hasPermission(event.player, LandFlag.SHEAR_BLOCK)
        }
    }

    @EventHandler
    fun handlePlayerBucketUse(event: PlayerBucketFillEvent) {
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.blockClicked.chunk))
        event.isCancelled = if (land != null) {
            if (land.hasMemberAccess(event.player.uniqueId)) return
            !hasPermission(event.player, LandFlag.BUCKET_INTERACT)
        } else {
            !hasPermission(event.player, LandFlag.BUCKET_INTERACT)
        }
    }

    @EventHandler
    fun handlePlayerBucketUse(event: PlayerBucketEmptyEvent) {
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.blockClicked.chunk))
        event.isCancelled = if (land != null) {
            if (land.hasMemberAccess(event.player.uniqueId)) return
            !hasPermission(event.player, LandFlag.BUCKET_INTERACT)
        } else {
            !hasPermission(event.player, LandFlag.BUCKET_INTERACT)
        }
    }

    @EventHandler
    fun handleEntityExplode(event: TNTPrimeEvent) {
        val block = event.primingBlock ?: event.block
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(block.chunk))
        val primerEntity = event.primingEntity
        if (land != null) {

            val landFlag = land.getFlag(LandFlag.EXPLOSIONS)
            val explosionsDisallowed = landFlag.getValue<Boolean>() == false
            val hasNoAccessAndNoPermissions = { entity: Entity ->
                !land.hasMemberAccess(entity.uniqueId) && !hasPermission(entity, LandFlag.EXPLOSIONS)
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
        event.blockList().groupBy(Block::getChunk).filter(this::filterForNoExplosiveLands)
            .forEach { event.blockList().removeAll(it.value) }
    }

    private fun filterForNoExplosiveLands(landEntry: Map.Entry<Chunk, List<Block>>): Boolean {
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(landEntry.key))
        return land?.getFlag(LandFlag.EXPLOSIONS)?.getValue<Boolean>() == false
    }

    private fun filterSpongeAbsorb(landEntry: Map.Entry<Chunk, List<BlockState>>): Boolean {
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(landEntry.key))
        return land?.getFlag(LandFlag.SPONGE_ABSORB)?.getValue<Boolean>() == false
    }
}