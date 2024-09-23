package net.onelitefeather.pandorascluster.api.player

interface Player {

    fun hasPermission(permission: String): Boolean

}