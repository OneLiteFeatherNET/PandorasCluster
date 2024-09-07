package net.onelitefeather.pandorascluster.api.models

import net.onelitefeather.pandorascluster.api.utils.LocationUtil

interface HomePosition {

    fun id(): Long

    fun posX(): Double

    fun posY(): Double

    fun posZ(): Double

    fun yaw(): Float

    fun pitch(): Float

    fun getBlockX(): Int = LocationUtil.Instances.instance.locToBlock(this.posX())

    fun getBlockY(): Int = LocationUtil.Instances.instance.locToBlock(this.posY())

    fun getBlockZ(): Int = LocationUtil.Instances.instance.locToBlock(this.posZ())
}