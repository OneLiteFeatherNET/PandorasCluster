package net.onelitefeather.pandorascluster.dbo.land

import net.onelitefeather.pandorascluster.dbo.chunk.ClaimedChunkDBO
import net.onelitefeather.pandorascluster.dbo.flag.FlagRoleAttachmentDBO
import net.onelitefeather.pandorascluster.dbo.player.LandMemberDBO
import net.onelitefeather.pandorascluster.dbo.player.LandPlayerDBO
import net.onelitefeather.pandorascluster.dbo.position.HomePositionDBO

interface LandDBO {

    fun id(): Long?

    fun owner(): LandPlayerDBO

    fun home(): HomePositionDBO

    fun members(): List<LandMemberDBO>

    fun chunks(): List<ClaimedChunkDBO>

    fun flags(): List<FlagRoleAttachmentDBO>

    fun world(): String
}