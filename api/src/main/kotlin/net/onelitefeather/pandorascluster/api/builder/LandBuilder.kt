package net.onelitefeather.pandorascluster.api.builder

import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk
import net.onelitefeather.pandorascluster.api.land.flag.FlagRoleAttachment
import net.onelitefeather.pandorascluster.api.player.LandMember
import net.onelitefeather.pandorascluster.api.player.LandPlayer
import net.onelitefeather.pandorascluster.api.position.HomePosition

class LandBuilder() {

    private var land = Land()

    constructor(init: LandBuilder.() -> Unit) : this() {
        init()
    }

    fun owner(attributes: () -> LandPlayer) {
        land = land.copy(owner = attributes())
    }

    fun homePosition(attributes: () -> HomePosition) {
        land = land.copy(home = attributes())
    }

    fun members(attributes: () -> List<LandMember>) {
        land = land.copy(members = attributes())
    }

    fun chunks(attributes: () -> List<ClaimedChunk>) {
        land = land.copy(chunks = attributes())
    }

    fun flags(attributes: () -> List<FlagRoleAttachment>) {
        land = land.copy(flags = attributes())
    }

    fun world(attributes: () -> String) {
        land = land.copy(world = attributes())
    }

    fun build(): Land {
        return land
    }
}

fun landBuilder(init: LandBuilder.() -> Unit) = LandBuilder(init).build()