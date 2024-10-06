package net.onelitefeather.pandorascluster.dbo.player

import net.onelitefeather.pandorascluster.api.enums.LandRole

interface LandMemberDBO {

    fun id(): Long?

    fun member(): LandPlayerDBO

    fun role(): LandRole
}