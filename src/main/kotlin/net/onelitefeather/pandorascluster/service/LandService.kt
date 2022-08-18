package net.onelitefeather.pandorascluster.service

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion
import io.sentry.Sentry
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.ChunkPlaceholder
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.land.player.LandMember
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.land.position.HomePosition
import net.onelitefeather.pandorascluster.listener.LandBlockListener
import net.onelitefeather.pandorascluster.listener.LandEntityListener
import net.onelitefeather.pandorascluster.listener.LandPlayerListener
import net.onelitefeather.pandorascluster.listener.LandWorldListener
import net.onelitefeather.pandorascluster.util.CHUNK_ROTATIONS
import net.onelitefeather.pandorascluster.util.getChunkIndex
import org.bukkit.Chunk
import org.bukkit.entity.Player
import org.hibernate.HibernateException
import java.util.*
import java.util.function.Consumer
import java.util.logging.Level


class LandService(
    private val pandorasClusterApi: PandorasClusterApi
) {

    init {
        val pluginManager = pandorasClusterApi.getPlugin().server.pluginManager
        pluginManager.registerEvents(LandBlockListener(this), pandorasClusterApi.getPlugin())
        pluginManager.registerEvents(LandEntityListener(this), pandorasClusterApi.getPlugin())
        pluginManager.registerEvents(LandPlayerListener(this), pandorasClusterApi.getPlugin())
        pluginManager.registerEvents(LandWorldListener(this), pandorasClusterApi.getPlugin())
    }

    fun getLands(): List<Land> {
        val lands: MutableList<Land> = ArrayList()
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val query = session.createQuery("SELECT l FROM Land l", Land::class.java)
                lands.addAll(query.list())
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Could not load lands.", e)
            Sentry.captureException(e)

        }
        return lands
    }

    fun hasPlayerLand(playerId: UUID): Boolean {
        val offlinePlayer = pandorasClusterApi.getPlugin().server.getOfflinePlayer(playerId)
        if (!offlinePlayer.hasPlayedBefore()) return false
        val landPlayer = pandorasClusterApi.getLandPlayer(playerId) ?: return false
        return getLand(landPlayer) != null
    }

    fun getHome(uuid: UUID): HomePosition? {
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val landOfOwner = session.createQuery(
                    "SELECT h FROM Land l JOIN l.homePosition h JOIN l.owner p WHERE p.uuid = :uuid",
                    HomePosition::class.java
                )
                landOfOwner.setParameter("uuid", uuid.toString())
                return landOfOwner.singleResult
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, cannotUpdateLand, e)
            Sentry.captureException(e)

        }
        return null
    }

    fun exists(land: Land): Boolean {
        val owner = land.owner ?: return false
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val query = session.createQuery(
                    "SELECT kdc FROM Land kdc JOIN FETCH kdc.owner o WHERE o.uuid = :playerId AND kdc.id = :id",
                    Land::class.java
                )
                query.maxResults = 1
                query.setParameter("playerId", owner.uuid)
                query.setParameter("id", land.id)
                return query.uniqueResult() != null
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Something went wrong!", e)
            Sentry.captureException(e)

            return false
        }
    }

    fun isChunkClaimed(chunk: Chunk): Boolean {
        return this.getFullLand(chunk) != null
    }

    fun findConnectedChunk(player: Player, consumer: Consumer<Land?>) {
        val chunk = player.chunk
        var land: Land? = null
        for (chunkRotation in CHUNK_ROTATIONS) {
            val connectedChunk = player.world.getChunkAt(
                chunk.x + chunkRotation.x,
                chunk.z + chunkRotation.z
            )
            val fullLand: Land? = this.getFullLand(connectedChunk)
            if (fullLand != null) {
                land = fullLand
            }
        }
        consumer.accept(land)
    }

    fun getLand(owner: LandPlayer): Land? {
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val landOfOwner = session.createQuery(
                    "SELECT l FROM Land l JOIN l.owner o JOIN FETCH l.chunks WHERE o.uuid = :uuid",
                    Land::class.java
                )
                landOfOwner.setParameter("uuid", owner.uuid)
                return landOfOwner.uniqueResult()
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, cannotUpdateLand, e)
            Sentry.captureException(e)

        }
        return null
    }

    fun getFullLand(chunk: Chunk): Land? {
        var land: Land? = null
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val query = session.createQuery(
                    "SELECT ch FROM ChunkPlaceholder ch JOIN FETCH ch.land WHERE ch.chunkIndex = :chunkIndex",
                    ChunkPlaceholder::class.java
                )
                query.setParameter("chunkIndex", getChunkIndex(chunk))
                val chunkHolder = query.uniqueResult()
                if (chunkHolder != null) {
                    land = chunkHolder.land
                }
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Could not found land", e)
            Sentry.captureException(e)

        }
        return land
    }

    fun getFlagsByLand(land: Land): List<LandFlagEntity> {
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val flagsOfLand = session.createQuery(
                    "SELECT f FROM LandFlagEntity f JOIN FETCH f.land l WHERE l.id = :landId",
                    LandFlagEntity::class.java
                )
                flagsOfLand.setParameter("landId", land.id)
                return flagsOfLand.list()
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, cannotLoadFlags, e)
            Sentry.captureException(e)

        }
        return listOf()
    }

    fun getLandFlag(landFlag: LandFlag, land: Land): LandFlagEntity? {
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val flagOfLand = session.createQuery(
                    "SELECT f FROM LandFlagEntity f JOIN FETCH f.land l WHERE l.id = :landId AND f.name = :name",
                    LandFlagEntity::class.java
                )
                flagOfLand.setParameter("landId", land.id)
                flagOfLand.setParameter("name", landFlag.name)
                return flagOfLand.uniqueResult()
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, cannotLoadFlags, e)
            Sentry.captureException(e)

        }
        return null
    }

    fun getLandMember(land: Land, landPlayer: LandPlayer): LandMember? {
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val landMemberQuery = session.createQuery(
                    "SELECT m FROM LandMember m JOIN FETCH m.land l JOIN FETCH m.member lm WHERE l.id = :landId AND lm.id = :memberId",
                    LandMember::class.java
                )
                landMemberQuery.setParameter("memberId", landPlayer.id)
                landMemberQuery.setParameter("landId", land.id)
                return landMemberQuery.uniqueResult()
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, cannotLoadFlags, e)
            Sentry.captureException(e)

        }

        return null
    }

    fun checkWorldGuardRegion(chunk: Chunk): Boolean {

        val world = BukkitAdapter.adapt(chunk.world)
        val minChunkX = chunk.x shl 4
        val minChunkZ = chunk.z shl 4
        val maxChunkX = minChunkX + 15
        val maxChunkZ = minChunkZ + 15

        val regionManager = WorldGuard.getInstance().platform.regionContainer.get(world)
        val region = ProtectedCuboidRegion(
            "check_wg_overlaps",
            BlockVector3.at(minChunkX, 0, minChunkZ),
            BlockVector3.at(maxChunkX, chunk.world.maxHeight, maxChunkZ))

        val regions = regionManager?.regions?.values
        return region.getIntersectingRegions(regions).isNotEmpty()
    }
}

private const val cannotLoadFlags = "Cannot load flags by land"
private const val cannotUpdateLand = "Cannot update land"