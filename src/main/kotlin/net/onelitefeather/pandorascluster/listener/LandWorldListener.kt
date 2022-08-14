package net.onelitefeather.pandorascluster.listener

import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.service.LandService
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.world.StructureGrowEvent

class LandWorldListener(private val landService: LandService) : Listener {

    @EventHandler
    fun handleLeavesDecay(event: LeavesDecayEvent) {
        val land: Land = landService.getFullLand(event.block.chunk) ?: return
        val landFlag = landService.getLandFlag(LandFlag.LEAVES_DECAY, land)?: return
        if (landFlag.getValue<Boolean>() == false) return
        event.isCancelled = true
    }

    @Suppress("kotlin:S3776")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun handleStructureGrow(event: StructureGrowEvent) {

        val blocks = event.blocks
        if (blocks.isEmpty()) return

        val originChunk = blocks.first().chunk
        val area = landService.getFullLand(originChunk)

        if (area == null) {
            blocks.reversed().forEachIndexed index@{ index, blockState ->
                val chunk = blockState.chunk
                if (landService.getFullLand(chunk) == null) {
                    blocks.removeAt(index)
                }
            }
            return
        } else {
            val origin = landService.getFullLand(originChunk)
            if (origin == null) {
                event.isCancelled = true
                return
            }
            blocks.reversed().forEachIndexed index@ { index, blockState ->
                val chunk = blockState.chunk
                if (landService.getFullLand(chunk) == null) {
                    blocks.removeAt(index)
                    return@index
                }
                val plot = landService.getFullLand(chunk)
                if (plot != origin) {
                    event.blocks.removeAt(index)
                }
            }
        }
        val origin = landService.getFullLand(originChunk)
        if (origin == null) {
            event.isCancelled = true
            return
        }
        blocks.reversed().forEachIndexed { index, blockState ->
            val land = landService.getFullLand(blockState.chunk)
            if (land != null && land != origin && !land.isMerged() && !origin.isMerged()) {
                event.blocks.removeAt(index)
            }
        }
    }
}