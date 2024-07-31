package net.onelitefeather.pandorascluster.listener

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.land.flag.LandFlag
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

        val land = pandorasClusterApi.getLand(event.player.chunk)
        if(land == null) {
            event.isCancelled = true
            return
        }

        if (land.hasAccess(event.player.uniqueId)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleLeavesDecay(event: LeavesDecayEvent) {
        val land = pandorasClusterApi.getLand(event.block.chunk) ?: return
        val landFlag = land.getLandFlag(LandFlag.LEAVES_DECAY)
        if (landFlag.getValue<Boolean>() == false) return
        event.isCancelled = true
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
        val origin = pandorasClusterApi.getLand(firstChunk)

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
        val plot = pandorasClusterApi.getLand(map.key)
        return map.value.map { pandorasClusterApi.getLand(it.chunk) }
            .firstOrNull { plot == null || !hasSameOwner(plot, it!!) } != null
    }
}
