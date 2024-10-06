package net.onelitefeather.pandorascluster.database.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.builder.landBuilder
import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk
import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.player.LandPlayer
import net.onelitefeather.pandorascluster.api.position.HomePosition
import net.onelitefeather.pandorascluster.api.service.DatabaseService
import net.onelitefeather.pandorascluster.api.service.LandService
import net.onelitefeather.pandorascluster.api.utils.LOGGER
import net.onelitefeather.pandorascluster.database.models.ClaimedChunkEntity
import net.onelitefeather.pandorascluster.database.models.LandEntity
import org.hibernate.HibernateException
import org.hibernate.Transaction
import java.time.Duration
import java.util.*

class DatabaseLandService(
    private val api: PandorasClusterApi,
    private val databaseService: DatabaseService
) : LandService {

    val landCache: LoadingCache<ClaimedChunk, Land> = Caffeine.newBuilder().maximumSize(5000)
        .expireAfterAccess(Duration.ofMinutes(5))
        .expireAfterWrite(Duration.ofMinutes(5))
        .refreshAfterWrite(Duration.ofMinutes(1)).build { key -> getLand(key) }

    val unclaimedChunkCache: LoadingCache<ClaimedChunk, Boolean> = Caffeine.newBuilder().maximumSize(10_000)
        .expireAfterAccess(Duration.ofMinutes(5))
        .expireAfterWrite(Duration.ofMinutes(5))
        .refreshAfterWrite(Duration.ofMinutes(1)).build { key -> isChunkClaimed(key) }

    override fun getLands(): List<Land> {
        try {
            databaseService.sessionFactory().openSession().use { session ->
                val query = session.createQuery("SELECT l FROM LandEntity l", LandEntity::class.java)
                val lands = query.list()
                return lands.mapNotNull { databaseService.landMapper().entityToModel(it) }
            }
        } catch (e: HibernateException) {
            LOGGER.throwing(this::class.java.simpleName, "getLands", e)
        }
        return emptyList()
    }

    override fun updateLandHome(homePosition: HomePosition, ownerId: UUID) {
        var transaction: Transaction? = null
        try {
            databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.merge(databaseService.homePositionMapper().modelToEntity(homePosition))
                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            LOGGER.throwing(this::class.java.simpleName, "updateLandHome", e)
        }
    }

    override fun updateLand(land: Land) {
        var transaction: Transaction? = null
        try {
            databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.merge(databaseService.landMapper().modelToEntity(land))
                transaction?.commit()
                updateLoadedChunks(land)
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            LOGGER.throwing(this::class.java.simpleName, "updateLand", e)
        }
    }

    override fun addClaimedChunk(chunk: ClaimedChunk, land: Land?) {
        var transaction: Transaction? = null
        val claimedChunkEntity =
            ClaimedChunkEntity(null, chunk.chunkIndex, databaseService.landMapper().modelToEntity(land) as LandEntity)
        try {

            databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.persist(claimedChunkEntity)
                transaction?.commit()
            }

            if (land != null) claimChunk(chunk, land)

        } catch (e: HibernateException) {
            transaction?.rollback()
            LOGGER.throwing(this::class.java.simpleName, "addClaimedChunk", e)
        }
    }

    override fun createLand(owner: LandPlayer, home: HomePosition, chunk: ClaimedChunk, world: String): Land? {

        val land = getLand(chunk)
        if (land != null) return land
        if (hasPlayerLand(owner)) return getLand(owner)

        var transaction: Transaction? = null
        try {
            databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                val homeEntity = databaseService.homePositionMapper().modelToEntity(home)
                session.persist(homeEntity)

                val createLand = landBuilder {
                    owner { owner }
                    homePosition { databaseService.homePositionMapper().entityToModel(homeEntity)!! }
                    world { world }
                    members { emptyList() }
                    chunks { listOf(chunk) }
                    flags { emptyList() }
                }

                val landEntity = databaseService.landMapper().modelToEntity(createLand) as LandEntity
                val chunkEntity = ClaimedChunkEntity(null, chunk.chunkIndex, landEntity)

                session.persist(landEntity)
                session.persist(chunkEntity)
                transaction?.commit()
                return databaseService.landMapper().entityToModel(landEntity)
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            LOGGER.throwing(this::class.java.simpleName, "createLand", e)
        }

        return null
    }

    override fun unclaimLand(land: Land) {
        var transaction: Transaction? = null
        try {
            databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()

                land.members.forEach { api.getLandPlayerService().removeLandMember(it) }

                land.chunks.map(ClaimedChunk::chunkIndex).forEach(this::removeClaimedChunk)
                land.flags.forEach { api.getLandFlagService().removeLandFlag(it, land) }

                session.remove(databaseService.landMapper().modelToEntity(land))
                session.remove(databaseService.homePositionMapper().modelToEntity(land.home))

                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            LOGGER.throwing(this::class.java.simpleName, "unclaimLand", e)
        }
    }

    override fun removeClaimedChunk(chunkIndex: Long): Boolean {
        var transaction: Transaction? = null
        try {
            databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()

                val chunk = getClaimedChunk(chunkIndex)
                if (chunk != null) {
                    val chunkEntity = databaseService.claimedChunkMapper().modelToEntity(chunk) as ClaimedChunkEntity
                    session.remove(chunkEntity)
                    transaction?.commit()
                    unclaimChunk(chunk)
                    return true
                }
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            LOGGER.throwing(this::class.java.simpleName, "removeClaimedChunk", e)
        }
        return false
    }

    override fun getLand(chunk: ClaimedChunk): Land? {
        var land = landCache.getIfPresent(chunk)
        if (land != null) return land

        try {
            databaseService.sessionFactory().openSession().use { session ->
                val query = session.createQuery(
                    "SELECT ch FROM ClaimedChunkEntity ch JOIN FETCH ch.landEntity WHERE ch.chunkIndex = :chunkIndex",
                    ClaimedChunkEntity::class.java
                )
                query.setParameter("chunkIndex", chunk.chunkIndex)
                val chunkHolder = query.uniqueResult()
                if (chunkHolder != null) {
                    land = databaseService.landMapper().entityToModel(chunkHolder.landEntity)
                }

                if (land != null) landCache.put(chunk, land)
            }
        } catch (e: HibernateException) {
            LOGGER.throwing(this::class.java.simpleName, "getLand", e)
        }

        return land
    }

    /**
     * @param chunk the chunk
     * @return true if the chunk is claimed.
     */
    override fun isChunkClaimed(chunk: ClaimedChunk): Boolean {
        val claimed = unclaimedChunkCache.getIfPresent(chunk)
        if (claimed == true) return true
        return getClaimedChunk(chunk.chunkIndex) != null
    }

    /**
     * @param owner the owner of the land
     */
    override fun getLand(owner: LandPlayer): Land? {
        try {
            databaseService.sessionFactory().openSession().use { session ->
                val query = session.createQuery(
                    "SELECT l FROM LandEntity l JOIN FETCH l.owner o WHERE o.uuid = :uuid",
                    LandEntity::class.java
                )
                query.setParameter("uuid", owner.uniqueId.toString())
                return databaseService.landMapper().entityToModel(query.uniqueResult())
            }
        } catch (e: HibernateException) {
            LOGGER.throwing(this::class.java.simpleName, "getLand", e)
        }

        return null
    }

    override fun hasPlayerLand(player: LandPlayer): Boolean {
        val landPlayer = api.getLandPlayerService().getLandPlayer(player.uniqueId) ?: return false
        return getLand(landPlayer) != null
    }

    override fun getClaimedChunk(chunkIndex: Long): ClaimedChunk? {
        try {
            databaseService.sessionFactory().openSession().use { session ->
                val query = session.createQuery(
                    "SELECT c FROM ClaimedChunkEntity c WHERE c.chunkIndex = :chunkIndex",
                    ClaimedChunkEntity::class.java
                )

                query.maxResults = 1
                query.setParameter("chunkIndex", chunkIndex)
                return databaseService.claimedChunkMapper().entityToModel(query.uniqueResult())
            }
        } catch (e: HibernateException) {
            LOGGER.throwing(this::class.java.simpleName, "isChunkClaimed", e)
        }

        return null
    }

    /**
     * @param chunk the chunk to claim.
     */
    private fun claimChunk(chunk: ClaimedChunk, land: Land) {
        unclaimedChunkCache.invalidate(chunk)
        landCache.put(chunk, land)
    }

    private fun unclaimChunk(chunk: ClaimedChunk) {
        unclaimedChunkCache.put(chunk, true)
        landCache.invalidate(chunk)
    }

    override fun updateLoadedChunks(land: Land) {
        landCache.invalidateAll(land.chunks)
        land.chunks.forEach {
            landCache.put(it, land)
        }
    }
}