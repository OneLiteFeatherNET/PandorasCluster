package net.onelitefeather.pandorascluster.api.position

import net.onelitefeather.pandorascluster.api.utils.LocationUtil

data class HomePosition(val id: Long?,
                        val posX: Double,
                        val posY: Double,
                        val posZ: Double,
                        val yaw: Float,
                        val pitch: Float) {

    fun getBlockX(): Int = LocationUtil.Instances.instance.locToBlock(posX)

    fun getBlockY(): Int = LocationUtil.Instances.instance.locToBlock(posY)

    fun getBlockZ(): Int = LocationUtil.Instances.instance.locToBlock(posZ)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HomePosition) return false

        if (id != other.id) return false
        if (posX != other.posX) return false
        if (posY != other.posY) return false
        if (posZ != other.posZ) return false
        if (yaw != other.yaw) return false
        if (pitch != other.pitch) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + posX.hashCode()
        result = 31 * result + posY.hashCode()
        result = 31 * result + posZ.hashCode()
        result = 31 * result + yaw.hashCode()
        result = 31 * result + pitch.hashCode()
        return result
    }

    override fun toString(): String {
        return "HomePosition(id=$id, posX=$posX, posY=$posY, posZ=$posZ, yaw=$yaw, pitch=$pitch)"
    }


}