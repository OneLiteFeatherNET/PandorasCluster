package net.onelitefeather.pandorascluster.service

import io.sentry.Sentry
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import org.hibernate.HibernateException
import org.hibernate.Transaction
import java.util.*
import java.util.logging.Level

class LandPlayerService(val pandorasClusterApi: PandorasClusterApi) {

    /**
     * @return all registered land players.
     */
    fun getPlayers(): List<LandPlayer> {
        try {

            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val query = session.createQuery(
                    "SELECT lp FROM LandPlayer lp",
                    LandPlayer::class.java
                )

                return query.list()
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Could not load players.", e)
            Sentry.captureException(e)
        }

        return listOf()
    }

    /**
     * @param uuid the uuid of the player.
     * @param name the name of the player.
     */
    fun createPlayer(uuid: UUID, name: String): Boolean {
        if(playerExists(uuid)) return false
        val landPlayer = LandPlayer(null, uuid.toString(), name)
        updateLandPlayer(landPlayer)
        return true
    }

    /**
     * @param uuid the uuid of the player.
     */
    fun deletePlayer(uuid: UUID) {
        val landPlayer = getLandPlayer(uuid)
        var transaction: Transaction? = null
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.remove(landPlayer)
                transaction?.commit()
            }
        } catch (e: HibernateException) {
            if (transaction != null) {
                transaction?.rollback()
                pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot delete player data for $uuid", e)
                Sentry.captureException(e)
            }
        }
    }

    /**
     * @param uuid the uuid of the player.
     * @return the land player by the uuid.
     */
    fun getLandPlayer(uuid: UUID): LandPlayer? {
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val chunkPlayerQuery = session.createQuery(
                    "SELECT lp FROM LandPlayer lp WHERE lp.uuid = :uuid",
                    LandPlayer::class.java
                )

                chunkPlayerQuery.maxResults = 1
                chunkPlayerQuery.setParameter("uuid", uuid.toString())
                return chunkPlayerQuery.uniqueResult()
            }

        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, couldNotLoadPlayerData, e)
            Sentry.captureException(e)
        }

        return null
    }

    /**
     * @param uuid the uuid of the player.
     * @return true if the player exists
     */
    fun playerExists(uuid: UUID): Boolean {
        var exists = false
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val chunkPlayerQuery = session.createQuery(
                    "SELECT lp FROM LandPlayer lp WHERE lp.uuid = :uuid",
                    LandPlayer::class.java
                )
                chunkPlayerQuery.setParameter("uuid", uuid.toString())
                exists = chunkPlayerQuery.uniqueResult() != null
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, couldNotLoadPlayerData, e)
            Sentry.captureException(e)
        }

        return exists
    }

    /**
     * @param landPlayer the player to update
     */
    fun updateLandPlayer(landPlayer: LandPlayer) {
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                session.beginTransaction()
                session.merge(landPlayer)
                session.transaction.commit()
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot update landplayer $landPlayer", e)
            Sentry.captureException(e)
        }
    }

    /**
     * @param name the name of the player.
     * @return the land player by the name.
     */
    fun getLandPlayer(name: String): LandPlayer? {
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val chunkPlayerQuery = session.createQuery(
                    "SELECT lp FROM LandPlayer lp WHERE lp.name = :name",
                    LandPlayer::class.java
                )
                chunkPlayerQuery.maxResults = 1
                chunkPlayerQuery.setParameter("name", name)
                return chunkPlayerQuery.uniqueResult()
            }

        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Could not load player data", e)
            Sentry.captureException(e)
        }

        return null
    }
}

private const val couldNotLoadPlayerData = "Could not load player data"
