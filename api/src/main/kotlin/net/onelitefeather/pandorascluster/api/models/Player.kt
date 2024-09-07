package net.onelitefeather.pandorascluster.api.models

interface Player {

    fun hasPermission(permission: String): Boolean

}