package net.onelitefeather.pandorascluster.dbo.player

interface LandPlayerDBO {

    fun id(): Long?

    fun uuid(): String

    fun name(): String
}