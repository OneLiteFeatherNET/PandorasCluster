package net.onelitefeather.pandorascluster.extensions

import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.service.LandService
import org.bukkit.Chunk

fun Chunk.getLand(landService: LandService): Land? {
    return landService.getFullLand(this)
}