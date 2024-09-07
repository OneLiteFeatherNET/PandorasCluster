package net.onelitefeather.pandorascluster.api.models.dto

interface PandorasLocation {

    fun x(): Double
    fun y(): Double
    fun z(): Double
    fun yaw(): Float
    fun pitch(): Float

}