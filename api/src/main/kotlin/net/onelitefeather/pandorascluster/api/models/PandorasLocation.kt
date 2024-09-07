package net.onelitefeather.pandorascluster.api.models

interface PandorasLocation {

    fun x(): Double
    fun y(): Double
    fun z(): Double
    fun yaw(): Float
    fun pitch(): Float

}