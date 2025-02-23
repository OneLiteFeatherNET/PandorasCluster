package net.onelitefeather.pandorascluster.database.service

import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.api.player.LandMember
import net.onelitefeather.pandorascluster.api.player.LandPlayer
import net.onelitefeather.pandorascluster.api.service.DatabaseService
import net.onelitefeather.pandorascluster.api.service.LandPlayerService
import net.onelitefeather.pandorascluster.api.service.LandService
import net.onelitefeather.pandorascluster.api.utils.LOGGER
import net.onelitefeather.pandorascluster.database.models.LandEntity
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity
import org.hibernate.HibernateException
import org.hibernate.Transaction
import java.util.*

class DatabaseLandPlayerService(private val databaseService: DatabaseService,
                                private val landService: LandService) : LandPlayerService {

    override fun addLandMember(land: Land, member: LandPlayer, landRole: LandRole?) {
        val role = landRole ?: LandRole.VISITOR

        val landEntity = databaseService.landMapper().modelToEntity(land) ?: return
        val memberEntity = databaseService.landPlayerMapper().modelToEntity(member) ?: return

        val landMember = LandMemberEntity(null, memberEntity as LandPlayerEntity, role, landEntity as LandEntity)
        var transaction: Transaction? = null
        try {
            databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.persist(landMember)
                transaction?.commit()
                landService.updateLand(land)
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            LOGGER.throwing(this::class.java.simpleName, "addLandMember", e)
        }
    }

    override fun updateLandMember(land: Land, member: LandMember) {
        try {

            val landEntity = databaseService.landMapper().modelToEntity(land) as LandEntity
            val memberEntity = databaseService.landMemberMapper().modelToEntity(member) as LandMemberEntity

            databaseService.sessionFactory().openSession().use { session ->
                session.beginTransaction()
                session.merge(memberEntity.copy(land = landEntity))
                session.transaction.commit()
                landService.updateLand(land)
            }
        } catch (e: HibernateException) {
            LOGGER.throwing(this::class.java.simpleName, "updateLandPlayer", e)
        }
    }

    override fun removeLandMember(member: LandMember) {
        var transaction: Transaction? = null
        try {
            databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.remove(databaseService.landMemberMapper().modelToEntity(member))
                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            LOGGER.throwing(this::class.java.simpleName, "removeLandMember", e)
        }
    }

    override fun getLandMember(land: Land, landPlayer: LandPlayer): LandMember? {
        return getLandMember(land, landPlayer.uniqueId)
    }

    override fun getLandMember(land: Land, uuid: UUID): LandMember? {
        return land.members.firstOrNull { it.member.uniqueId == uuid }
    }

    override fun getLandPlayers(): List<LandPlayer> {
        try {
            databaseService.sessionFactory().openSession().use { session ->
                val query = session.createQuery("SELECT lp FROM LandPlayerEntity lp", LandPlayerEntity::class.java)
                val players = query.list()
                return players.mapNotNull { databaseService.landPlayerMapper().entityToModel(it) }
            }
        } catch (e: HibernateException) {
            LOGGER.throwing(this::class.java.simpleName, "getLandPlayers", e)
        }

        return listOf()
    }

    override fun createPlayer(uuid: UUID, name: String): Boolean {
        if (playerExists(uuid)) return false
        val landPlayerEntity = LandPlayerEntity(null, uuid.toString(), name)
        var transaction: Transaction? = null
        try {
            databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.persist(landPlayerEntity)
                transaction?.commit()
            }
            return true
        } catch (e: HibernateException) {
            transaction?.rollback()
            LOGGER.throwing(this::class.java.simpleName, "addLandFlag", e)
        }

        return false
    }

    override fun deletePlayer(uuid: UUID) {
        val landPlayer = getLandPlayer(uuid) ?: return

        val land = landService.getLand(landPlayer)

        if(land != null) {
            landService.unclaimLand(land)
        }

        var transaction: Transaction? = null
        try {
           databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.remove(databaseService.landPlayerMapper().modelToEntity(landPlayer))
                transaction?.commit()
            }
        } catch (e: HibernateException) {
            if (transaction != null) {
                transaction?.rollback()
                LOGGER.throwing(this::class.java.simpleName, "deletePlayer", e)
            }
        }
    }

    override fun getLandPlayer(uuid: UUID): LandPlayer? {
        try {
            databaseService.sessionFactory().openSession().use { session ->
                val chunkPlayerQuery = session.createQuery(
                    "SELECT lp FROM LandPlayerEntity lp WHERE lp.uuid = :uuid",
                    LandPlayerEntity::class.java
                )

                chunkPlayerQuery.maxResults = 1
                chunkPlayerQuery.setParameter("uuid", uuid.toString())
                return databaseService.landPlayerMapper().entityToModel(chunkPlayerQuery.uniqueResult())
            }

        } catch (e: HibernateException) {
            LOGGER.throwing(this::class.java.simpleName, "getLandPlayer", e)
        }

        return null
    }

    override fun getLandPlayer(name: String): LandPlayer? {
        try {
            databaseService.sessionFactory().openSession().use { session ->
                val chunkPlayerQuery = session.createQuery("SELECT lp FROM LandPlayerEntity lp WHERE lp.name = :name",
                    LandPlayerEntity::class.java
                )
                chunkPlayerQuery.maxResults = 1
                chunkPlayerQuery.setParameter("name", name)
                return databaseService.landPlayerMapper().entityToModel(chunkPlayerQuery.uniqueResult())
            }

        } catch (e: HibernateException) {
            LOGGER.throwing(this::class.java.simpleName, "getLandPlayer", e)
        }

        return null
    }

    override fun playerExists(uuid: UUID): Boolean = getLandPlayer(uuid) != null

    override fun updateLandPlayer(landPlayer: LandPlayer) {
        try {
            databaseService.sessionFactory().openSession().use { session ->
                session.beginTransaction()
                session.merge(databaseService.landPlayerMapper().modelToEntity(landPlayer))
                session.transaction.commit()
            }
        } catch (e: HibernateException) {
            LOGGER.throwing(this::class.java.simpleName, "updateLandPlayer", e)
        }
    }
}