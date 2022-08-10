package net.onelitefeather.pandorascluster.service

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.builder.LandBuilder
import net.onelitefeather.pandorascluster.enums.ChunkRotation
import net.onelitefeather.pandorascluster.enums.LandRole
import net.onelitefeather.pandorascluster.land.ChunkPlaceholder
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.land.flag.LandFlagType
import net.onelitefeather.pandorascluster.land.player.LandMember
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.land.position.HomePosition
import net.onelitefeather.pandorascluster.listener.LandBlockListener
import net.onelitefeather.pandorascluster.listener.LandEntityListener
import net.onelitefeather.pandorascluster.listener.LandPlayerListener
import net.onelitefeather.pandorascluster.listener.LandWorldListener
import net.onelitefeather.pandorascluster.util.ChunkUtil
import org.bukkit.Chunk
import org.bukkit.entity.Player
import org.hibernate.HibernateException
import org.hibernate.Transaction
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.logging.Level

class LandService(private val pandorasClusterApi: PandorasClusterApi) {

    init {
        val pluginManager = pandorasClusterApi.plugin.server.pluginManager
        pluginManager.registerEvents(LandBlockListener(this), pandorasClusterApi.plugin)
        pluginManager.registerEvents(LandEntityListener(this), pandorasClusterApi.plugin)
        pluginManager.registerEvents(LandPlayerListener(this), pandorasClusterApi.plugin)
        pluginManager.registerEvents(LandWorldListener(this), pandorasClusterApi.plugin)
    }

    fun getLands(): List<Land>? {
        val lands: MutableList<Land> = ArrayList()
        try {
            pandorasClusterApi.sessionFactory.openSession().use { session ->
                val query = session.createQuery("SELECT l FROM Land l", Land::class.java)
                lands.addAll(query.list())
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.logger.log(Level.SEVERE, "Could not load lands.", e)
        }
        return lands
    }

    fun hasPlayerLand(playerId: UUID): Boolean {
        val offlinePlayer = pandorasClusterApi.plugin.server.getOfflinePlayer(playerId)
        if (!offlinePlayer.hasPlayedBefore()) return false
        val landPlayer = pandorasClusterApi.getLandPlayer(playerId) ?: return false
        return getLand(landPlayer) != null
    }

    fun getHome(uuid: UUID): HomePosition? {
        try {
            pandorasClusterApi.sessionFactory.openSession().use { session ->
                val landOfOwner = session.createQuery(
                    "SELECT h FROM Land l JOIN l.homePosition h JOIN l.owner p WHERE p.uuid = :uuid",
                    HomePosition::class.java
                )
                landOfOwner.setParameter("uuid", uuid.toString())
                return landOfOwner.singleResult
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.logger.log(Level.SEVERE, "Cannot update land", e)
        }
        return null
    }

    fun createLand(owner: LandPlayer, player: Player, chunk: Chunk) {
        if (!hasPlayerLand(owner.uniqueId)) {
            var transaction: Transaction? = null
            try {
                pandorasClusterApi.sessionFactory.openSession().use { session ->
                    transaction = session.beginTransaction()
                    val homePosition = HomePosition.of(player.location)
                    session.persist(homePosition)
                    val land =
                        LandBuilder().owner(owner).home(homePosition).world(player.world).chunkX(chunk.x)
                            .chunkZ(chunk.z).members(listOf()).mergedChunks(listOf()).build()
                    session.persist(land)
                    addFlags(land)
                    transaction?.commit()
                    addChunkPlaceholder(chunk, land)
                }
            } catch (e: HibernateException) {
                transaction?.rollback()
                pandorasClusterApi.logger.log(Level.SEVERE, "Cannot update land", e)
            }
        }
    }

    fun deletePlayerLand(player: Player) {
        val landPlayer = pandorasClusterApi.getLandPlayer(player.uniqueId) ?: return
        val land: Land = getLand(landPlayer) ?: return
        val world = player.server.getWorld(land.world) ?: return
        if (exists(land)) {
            var transaction: Transaction? = null
            try {
                pandorasClusterApi.sessionFactory.openSession().use { session ->
                    transaction = session.beginTransaction()
                    session.remove(land)
                    transaction?.commit()
                }
            } catch (e: HibernateException) {
                transaction?.rollback()
                pandorasClusterApi.logger.log(Level.SEVERE, "Cannot remove the land from the database.", e)
            }
        }
    }

    private fun exists(land: Land): Boolean {
        try {
            pandorasClusterApi.sessionFactory.openSession().use { session ->
                val kitCooldown = session.createQuery(
                    "SELECT kdc FROM Land kdc WHERE playerId = :playerId AND id = :id",
                    Land::class.java
                )
                kitCooldown.maxResults = 1
                kitCooldown.setParameter("playerId", land.owner.uniqueId.toString())
                kitCooldown.setParameter("id", land.id)
                return kitCooldown.uniqueResult() != null
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.logger.log(Level.SEVERE, "Something went wrong!", e)
            return false
        }
    }

    fun isChunkClaimed(chunk: Chunk): Boolean {
        return this.getFullLand(chunk) != null
    }

    fun findConnectedChunk(player: Player, consumer: Consumer<Land?>) {
        val chunk = player.chunk
        var land: Land? = null
        for (chunkRotation in ChunkRotation.BY_NAME.values) {
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
            pandorasClusterApi.sessionFactory.openSession().use { session ->
                val landOfOwner = session.createQuery(
                    "SELECT l FROM Land l JOIN l.owner o JOIN FETCH l.chunks WHERE o.uuid = :uuid",
                    Land::class.java
                )
                landOfOwner.setParameter("uuid", owner.uniqueId.toString())
                return landOfOwner.uniqueResult()
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.logger.log(Level.SEVERE, "Cannot update land", e)
        }
        return null
    }

    fun getFullLand(chunk: Chunk): Land? {
        var land: Land? = null
        try {
            pandorasClusterApi.sessionFactory.openSession().use { session ->
                val query = session.createQuery(
                    "SELECT ch FROM ChunkPlaceholder ch JOIN FETCH ch.land WHERE ch.chunkIndex = :chunkIndex",
                    ChunkPlaceholder::class.java
                )
                query.setParameter("chunkIndex", ChunkUtil.getChunkIndex(chunk))
                val chunkHolder = query.uniqueResult()
                if (chunkHolder != null) {
                    land = chunkHolder.land
                }
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.logger.log(Level.SEVERE, "Cannot update land", e)
        }
        return land
    }

    fun setLandOwner(land: Land, landPlayer: LandPlayer) {
        land.owner = landPlayer
        updateLand(land)
    }

    fun addLandMember(land: Land, member: LandPlayer, landRole: LandRole?) {
        val landMember = LandMember(member, landRole ?: LandRole.MEMBER, land)
        var transaction: Transaction? = null
        try {
            pandorasClusterApi.sessionFactory.openSession().use { session ->
                transaction = session.beginTransaction()
                session.persist(landMember)
                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.logger.log(
                Level.SEVERE,
                "Cannot save land member $landMember",
                e
            )
        }
    }

    fun addChunkPlaceholder(chunk: Chunk?, land: Land?) {
        var transaction: Transaction? = null
        val chunkPlaceholder = ChunkPlaceholder(ChunkUtil.getChunkIndex(chunk!!), land)
        try {
            pandorasClusterApi.sessionFactory.openSession().use { session ->
                transaction = session.beginTransaction()
                session.persist(chunkPlaceholder)
                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.logger.log(
                Level.SEVERE,
                "Cannot save chunk placeholder $chunkPlaceholder",
                e
            )
        }
    }

    private fun updateLand(land: Land) {
        var transaction: Transaction? = null
        try {
            pandorasClusterApi.sessionFactory.openSession().use { session ->
                transaction = session.beginTransaction()
                session.merge(land)
                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.logger.log(Level.SEVERE, String.format("Cannot update land %s", land), e)
        }
    }

    fun updateLandFlag(landFlagEntity: LandFlagEntity) {
        var transaction: Transaction? = null
        try {
            pandorasClusterApi.sessionFactory.openSession().use { session ->
                transaction = session.beginTransaction()
                session.merge(landFlagEntity)
                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.logger.log(Level.SEVERE, String.format("Cannot update landflag %s", landFlagEntity), e)
        }
    }

    fun getLandFlag(landFlag: LandFlag, land: Land): LandFlagEntity? {
        try {
            pandorasClusterApi.sessionFactory.openSession().use { session ->
                val flagOfLand = session.createQuery(
                    "SELECT f FROM LandFlagEntity f JOIN FETCH f.land l WHERE l.id = :landId AND f.name = :name",
                    LandFlagEntity::class.java
                )
                flagOfLand.setParameter("landId", land.id)
                flagOfLand.setParameter("name", landFlag.name)
                return flagOfLand.uniqueResult()
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.logger.log(Level.SEVERE, "Cannot load flags by land", e)
        }
        return null
    }

    fun getFlagsByLand(land: Land): List<LandFlagEntity> {
        try {
            pandorasClusterApi.sessionFactory.openSession().use { session ->
                val flagsOfLand = session.createQuery(
                    "SELECT f FROM LandFlagEntity f JOIN FETCH f.land l WHERE l.id = :landId",
                    LandFlagEntity::class.java
                )
                flagsOfLand.setParameter("landId", land.id)
                return flagsOfLand.list()
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.logger.log(Level.SEVERE, "Cannot load flags by land", e)
        }
        return listOf()
    }

    private fun addFlags(land: Land) {
        CompletableFuture.runAsync {
            val flagEntities: MutableList<LandFlagEntity> = ArrayList()
            for (landFlag in LandFlag.FLAG_HASHMAP.values) {

                if (landFlag.flagType == LandFlagType.UNKNOWN) continue
                flagEntities.add(
                    LandFlagEntity(
                        null,
                        landFlag.name,
                        landFlag.defaultValue.toString(),
                        landFlag.type,
                        landFlag.flagType,
                        land
                    )
                )
            }
            var transaction: Transaction? = null
            try {
                pandorasClusterApi.sessionFactory.openSession().use { session ->

                    transaction = session.beginTransaction()
                    for (landFlag in flagEntities) {
                        session.persist(landFlag)
                    }

                    transaction?.commit()
                }
            } catch (e: HibernateException) {
                transaction?.rollback()
                pandorasClusterApi.logger.log(Level.SEVERE, "Cannot add flags to land $land", e)
            }
        }
    }

}