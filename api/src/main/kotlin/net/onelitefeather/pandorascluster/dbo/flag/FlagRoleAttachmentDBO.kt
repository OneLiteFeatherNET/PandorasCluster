package net.onelitefeather.pandorascluster.dbo.flag

import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag

interface FlagRoleAttachmentDBO {

    fun id(): Long?

    fun role(): LandRole

    fun flag(): LandFlag
}