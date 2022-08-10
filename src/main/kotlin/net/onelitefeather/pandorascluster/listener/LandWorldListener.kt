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
        if (landFlag.getValue()!!) return
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun handleStructureGrow(event: StructureGrowEvent) {

        val blocks = event.blocks
        if (blocks.isEmpty()) return

        var location = blocks[0].location
        val area = landService.getFullLand(location.chunk)

        if (area == null) {
            for (i in blocks.indices.reversed()) {
                location = blocks[i].location
                if (landService.getFullLand(location.chunk) == null) {
                    blocks.removeAt(i)
                }
            }
            return
        } else {
            val origin = landService.getFullLand(location.chunk)
            if (origin == null) {
                event.isCancelled = true
                return
            }
            for (i in blocks.indices.reversed()) {
                location = blocks[i].location
                if (landService.getFullLand(location.chunk) == null) {
                    blocks.removeAt(i)
                    continue
                }
                val plot = landService.getFullLand(location.chunk)
                if (plot != origin) {
                    event.blocks.removeAt(i)
                }
            }
        }
        val origin = landService.getFullLand(location.chunk)
        if (origin == null) {
            event.isCancelled = true
            return
        }
        for (i in blocks.indices.reversed()) {
            location = blocks[i].location
            val land = landService.getFullLand(location.chunk)
            if (land != null && land != origin && !land.isMerged && !origin.isMerged) {
                event.blocks.removeAt(i)
            }
        }
    }
}