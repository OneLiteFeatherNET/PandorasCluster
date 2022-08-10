package net.onelitefeather.pandorascluster.util

import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.land.flag.LandFlagType
import net.onelitefeather.pandorascluster.land.position.HomePosition
import net.onelitefeather.pandorascluster.service.LandPlayerService

val DUMMY_LAND = Land(
    LandPlayerService.DUMMY,
    HomePosition.dummyLocation(),
    "world",
    -1, -1
)

val DUMMY_FLAG_ENTITY = LandFlagEntity(
    -1,
    "dummy",
    "dummy",
    0,
    LandFlagType.UNKNOWN,
    DUMMY_LAND
)

