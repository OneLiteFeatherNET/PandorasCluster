package net.onelitefeather.pandorascluster.api.utils

interface NumberUtil {

    fun floor(num: Double): Int {
        val floor = num.toInt()
        return if (floor.toDouble() == num) floor else floor - (java.lang.Double.doubleToRawLongBits(num) ushr 63).toInt()
    }

    fun locToBlock(loc: Double): Int {
        return floor(loc)
    }
}
