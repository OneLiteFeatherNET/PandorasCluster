package net.onelitefeather.pandorascluster.api.service

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk
import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.land.flag.FlagRoleAttachment
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper
import net.onelitefeather.pandorascluster.api.player.LandMember
import net.onelitefeather.pandorascluster.api.player.LandPlayer
import net.onelitefeather.pandorascluster.api.position.HomePosition
import net.onelitefeather.pandorascluster.dbo.chunk.ClaimedChunkDBO
import net.onelitefeather.pandorascluster.dbo.flag.FlagRoleAttachmentDBO
import net.onelitefeather.pandorascluster.dbo.land.LandDBO
import net.onelitefeather.pandorascluster.dbo.player.LandMemberDBO
import net.onelitefeather.pandorascluster.dbo.player.LandPlayerDBO
import net.onelitefeather.pandorascluster.dbo.position.HomePositionDBO
import org.hibernate.SessionFactory

interface DatabaseService {

    fun connect(configFileResource: String)

    fun shutdown()

    fun isRunning(): Boolean

    fun sessionFactory(): SessionFactory

    fun landMapper(): DatabaseEntityMapper<LandDBO, Land>
    fun landMemberMapper(): DatabaseEntityMapper<LandMemberDBO, LandMember>
    fun landPlayerMapper(): DatabaseEntityMapper<LandPlayerDBO, LandPlayer>
    fun claimedChunkMapper(): DatabaseEntityMapper<ClaimedChunkDBO, ClaimedChunk>
    fun homePositionMapper(): DatabaseEntityMapper<HomePositionDBO, HomePosition>
    fun flagMapper(): DatabaseEntityMapper<FlagRoleAttachmentDBO, FlagRoleAttachment>
}