package net.onelitefeather.pandorascluster.util

import net.onelitefeather.pandorascluster.api.utils.LocationUtil
import org.bukkit.Location

class BukkitLocationUtil : LocationUtil {
    override fun locToBlock(input: Double): Int {
        return Location.locToBlock(input)
    }
}