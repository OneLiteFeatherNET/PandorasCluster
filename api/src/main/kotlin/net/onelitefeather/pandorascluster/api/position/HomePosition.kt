package net.onelitefeather.pandorascluster.api.position

data class HomePosition(val id: Long?,
                        val posX: Double,
                        val posY: Double,
                        val posZ: Double,
                        val yaw: Float,
                        val pitch: Float) {

    constructor() : this(null, 0.0, 0.0, 0.0, 0.0F, 0.0F)

    fun getBlockX(): Int = locToBlock(posX)

    fun getBlockY(): Int = locToBlock(posY)

    fun getBlockZ(): Int = locToBlock(posZ)

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

    private fun floor(num: Double): Int {
        val floor = num.toInt()
        return if (floor.toDouble() == num) floor else floor - (java.lang.Double.doubleToRawLongBits(num) ushr 63).toInt()
    }

    private fun locToBlock(loc: Double): Int {
        return floor(loc)
    }

}