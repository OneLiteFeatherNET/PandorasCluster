package net.onelitefeather.pandorascluster.api

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk
import net.onelitefeather.pandorascluster.api.position.HomePosition
import java.util.*

val updatedHome = HomePosition(null, 185.0, 27.0, 75.0, 0.0F, 0.0F)
val landHome = HomePosition(null, 180.0, 25.0, 60.0, 0.0F, 0.0F)
val mainChunk = ClaimedChunk(null, -12455436)
val landOwnerUUID: UUID = UUID.fromString("df7c66a6-7876-44c2-9abd-9bd33f7d3d9e")

const val randomBound = 199999999L
val random = Random()