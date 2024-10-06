package net.onelitefeather.pandorascluster.database.service

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk
import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.land.flag.FlagRoleAttachment
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper
import net.onelitefeather.pandorascluster.api.player.LandMember
import net.onelitefeather.pandorascluster.api.player.LandPlayer
import net.onelitefeather.pandorascluster.api.position.HomePosition
import net.onelitefeather.pandorascluster.api.service.DatabaseService
import net.onelitefeather.pandorascluster.api.utils.ThreadHelper
import net.onelitefeather.pandorascluster.database.mapper.impl.*
import net.onelitefeather.pandorascluster.dbo.chunk.ClaimedChunkDBO
import net.onelitefeather.pandorascluster.dbo.flag.FlagRoleAttachmentDBO
import net.onelitefeather.pandorascluster.dbo.land.LandDBO
import net.onelitefeather.pandorascluster.dbo.player.LandMemberDBO
import net.onelitefeather.pandorascluster.dbo.player.LandPlayerDBO
import net.onelitefeather.pandorascluster.dbo.position.HomePositionDBO
import org.hibernate.HibernateException
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration

class DatabaseServiceImpl : DatabaseService, ThreadHelper {

    lateinit var sessionFactory: SessionFactory

    override fun connect(configFileResource: String) {
        syncThreadForServiceLoader {
            try {
                sessionFactory = Configuration().configure().configure(configFileResource).buildSessionFactory()
            } catch (e: HibernateException) {
                throw HibernateException("Cannot build session factorty.", e)
            }
        }
    }

    override fun shutdown() {
        sessionFactory.close()
    }

    override fun isRunning(): Boolean = sessionFactory.isOpen

    override fun sessionFactory(): SessionFactory = sessionFactory

    override fun landMapper(): DatabaseEntityMapper<LandDBO, Land> = LandEntityMapper(this)

    override fun landMemberMapper(): DatabaseEntityMapper<LandMemberDBO, LandMember> = LandMemberMapper(this)

    override fun landPlayerMapper(): DatabaseEntityMapper<LandPlayerDBO, LandPlayer> = LandPlayerMapper()

    override fun claimedChunkMapper(): DatabaseEntityMapper<ClaimedChunkDBO, ClaimedChunk> = ClaimedChunkMapper()

    override fun homePositionMapper(): DatabaseEntityMapper<HomePositionDBO, HomePosition> = HomePositionMapper()

    override fun flagMapper(): DatabaseEntityMapper<FlagRoleAttachmentDBO, FlagRoleAttachment> = FlagRoleAttachmentMapper()
}