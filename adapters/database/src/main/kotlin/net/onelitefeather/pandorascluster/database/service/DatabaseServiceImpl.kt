package net.onelitefeather.pandorascluster.database.service

import net.onelitefeather.pandorascluster.api.service.DatabaseService
import net.onelitefeather.pandorascluster.api.utils.ThreadHelper
import net.onelitefeather.pandorascluster.database.mapper.impl.*
import org.hibernate.HibernateException
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import java.nio.file.Path

class DatabaseServiceImpl : DatabaseService, ThreadHelper {

    lateinit var sessionFactory: SessionFactory

    override fun connect(configFilePath: Path) {
        syncThreadForServiceLoader {
            try {
                sessionFactory = Configuration().configure().configure(configFilePath.toFile()).buildSessionFactory()
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

    override fun landMapper(): LandEntityMapper = LandEntityMapper(this)

    override fun landMemberMapper(): LandMemberMapper = LandMemberMapper(this)

    override fun landPlayerMapper(): LandPlayerMapper =  LandPlayerMapper()

    override fun claimedChunkMapper(): ClaimedChunkMapper = ClaimedChunkMapper()

    override fun homePositionMapper(): HomePositionMapper = HomePositionMapper()

    override fun flagMapper(): FlagRoleAttachmentMapper = FlagRoleAttachmentMapper()
}