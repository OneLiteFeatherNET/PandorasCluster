package net.onelitefeather.pandorascluster.builder

import net.onelitefeather.pandorascluster.land.ChunkPlaceholder
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.land.player.LandMember
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.land.position.HomePosition
import org.bukkit.World

class LandBuilder() {

    private var land = Land()

    constructor(init: LandBuilder.() -> Unit) : this() {
        init()
    }

    fun owner(attributes: () -> LandPlayer) {
        land = land.copy(owner = attributes())
    }

    fun homePosition(attributes: () -> HomePosition) {
        land = land.copy(homePosition = attributes())
    }

    fun members(attributes: () -> List<LandMember>) {
        land = land.copy(landMembers = attributes())
    }

    fun chunks(attributes: () -> List<ChunkPlaceholder>) {
        land = land.copy(chunks = attributes())
    }

    fun flags(attributes: () -> List<LandFlagEntity>) {
        land = land.copy(flags = attributes())
    }

    fun world(attributes: () -> World) {
        land = land.copy(world = attributes().name)
    }

    fun chunkX(attributes: () -> Int) {
        land = land.copy(x = attributes())
    }

    fun chunkZ(attributes: () -> Int) {
        land = land.copy(z = attributes())
    }

    fun build() : Land {
        return land
    }
}

fun landBuilder(init: LandBuilder.() -> Unit) = LandBuilder(init).build()