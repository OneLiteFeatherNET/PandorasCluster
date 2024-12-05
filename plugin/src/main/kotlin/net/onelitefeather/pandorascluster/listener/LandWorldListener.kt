package net.onelitefeather.pandorascluster.listener

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import org.bukkit.Chunk
import org.bukkit.block.BlockState
import org.bukkit.event.*
import org.bukkit.event.block.BlockFertilizeEvent
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.raid.RaidTriggerEvent
import org.bukkit.event.world.StructureGrowEvent

class LandWorldListener(private val pandorasClusterApi: PandorasClusterApi) :
    Listener, ChunkUtils {

    @EventHandler
    fun handleRaidStart(event: RaidTriggerEvent) {

        val land = pandorasClusterApi.getLandService().getLand(event.player.chunk.chunkKey)
        if(land == null) {
            event.isCancelled = true
            return
        }

        if (land.hasMemberAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleLeavesDecay(event: LeavesDecayEvent) {
        val land = pandorasClusterApi.getLandService().getLand(event.block.chunk.chunkKey) ?: return
        event.isCancelled = !land.hasFlag(LandFlag.LEAVES_DECAY)
    }

    @EventHandler
    fun handleBlockFertilize(event: BlockFertilizeEvent) {
        handle(event)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun handleStructureGrow(event: StructureGrowEvent) {
        handle(event)
    }

    private fun handle(event: Event) {

        val blocks = when (event) {
            is BlockFertilizeEvent -> event.blocks
            is StructureGrowEvent -> event.blocks
            else -> listOf()
        }

        if (blocks.isEmpty()) return

        val blocksByChunks = blocks.groupBy(BlockState::getChunk)
        val firstChunk = blocks.first().chunk
        val origin = pandorasClusterApi.getLandService().getLand(firstChunk.chunkKey)

        if (origin == null && event is Cancellable) {
            event.isCancelled = true
            return
        }

        blocksByChunks.filter(this::filterHasSameOwner).values.reduceOrNull(this::combineBlockStates)?.forEach {
            if (event is BlockFertilizeEvent) event.blocks.remove(it)
            if (event is StructureGrowEvent) event.blocks.remove(it)
        }
    }

    private fun combineBlockStates(origin: List<BlockState>, add: List<BlockState>): List<BlockState> {
        return origin.plus(add)
    }

    private fun filterHasSameOwner(map: Map.Entry<Chunk, List<BlockState>>): Boolean {
        val plot = pandorasClusterApi.getLandService().getLand(map.key.chunkKey)
        return map.value.firstOrNull {
            val otherLand = pandorasClusterApi.getLandService().getLand(it.chunk.chunkKey)
            plot == null || !hasSameOwner(plot, otherLand!!)
        } != null
    }
}
