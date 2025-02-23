package net.onelitefeather.pandorascluster.extensions

import net.onelitefeather.pandorascluster.api.position.HomePosition
import org.bukkit.Location
import org.bukkit.World

interface LocationUtils {

    fun fromHomePosition(world: World, home: HomePosition): Location {
        return Location(world, home.posX, home.posY, home.posZ, home.yaw, home.pitch)
    }

    fun toHomePosition(location: Location): HomePosition {
        return HomePosition(null, location.x, location.y, location.z, location.yaw, location.pitch)
    }
}