package net.onelitefeather.pandorascluster.listener

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.util.hasSameOwner
import org.bukkit.event.*
import org.bukkit.event.block.BlockFertilizeEvent
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.raid.RaidTriggerEvent
import org.bukkit.event.world.StructureGrowEvent

class LandWorldListener(private val pandorasClusterApi: PandorasClusterApi) :
    Listener {

    @EventHandler
    fun handleRaidStart(event: RaidTriggerEvent) {
        val land = pandorasClusterApi.getLand(event.player.chunk)
        if (Permission.TRIGGER_RAID.hasPermission(event.player)) return
        if (land == null || !land.hasAccess(event.player.uniqueId)) return
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
        val blocksByChunks = blocks.groupBy { it.chunk }
        val firstChunk = blocks.first().chunk
        val origin = pandorasClusterApi.getLand(firstChunk)
        if (origin == null && event is Cancellable) {
            event.isCancelled = true
            return
        }

        val result = blocksByChunks.filter { entry ->
            val plot = pandorasClusterApi.getLand(entry.key)
            plot == null || !hasSameOwner(plot, origin!!)
        }.values.reduceOrNull { acc, blockStates -> acc + blockStates }
        result?.forEach {
            if(event is BlockFertilizeEvent) event.blocks.remove(it)
            if(event is StructureGrowEvent) event.blocks.remove(it)
        }
    }
}