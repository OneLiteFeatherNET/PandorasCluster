package net.onelitefeather.pandorascluster.dbo.position

interface HomePositionDBO {

    fun id(): Long?

    fun posX(): Double

    fun posY(): Double

    fun posZ(): Double

    fun yaw(): Float

    fun pitch(): Float
}