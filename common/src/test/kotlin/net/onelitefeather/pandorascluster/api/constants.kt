package net.onelitefeather.pandorascluster.api

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk
import net.onelitefeather.pandorascluster.api.position.HomePosition
import java.util.*

val updatedHome = HomePosition(null, 185.0, 27.0, 75.0, 0.0F, 0.0F)
val landHome = HomePosition(null, 180.0, 25.0, 60.0, 0.0F, 0.0F)
val mainChunk = ClaimedChunk(null, -12455436)
val landOwnerUUID: UUID = UUID.fromString("05bf52c6-7bb0-4f13-8951-0e1fd803df35")

const val randomBound = 199999999L
val random = Random()