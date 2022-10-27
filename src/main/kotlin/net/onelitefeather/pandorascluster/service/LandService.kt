package net.onelitefeather.pandorascluster.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion
import io.sentry.Sentry
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.ChunkPlaceholder
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.player.LandMember
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.land.position.HomePosition
import net.onelitefeather.pandorascluster.listener.LandContainerProtectionListener
import net.onelitefeather.pandorascluster.listener.LandWorldListener
import net.onelitefeather.pandorascluster.listener.block.LandBlockListener
import net.onelitefeather.pandorascluster.listener.entity.LandEntityListener
import net.onelitefeather.pandorascluster.listener.entity.LandHangingEntityListener
import net.onelitefeather.pandorascluster.listener.entity.LandVehicleListener
import net.onelitefeather.pandorascluster.listener.player.LandPlayerInteractListener
import net.onelitefeather.pandorascluster.listener.player.PlayerConnectionListener
import net.onelitefeather.pandorascluster.listener.player.PlayerInteractEntityListener
import net.onelitefeather.pandorascluster.listener.player.PlayerLocationListener
import net.onelitefeather.pandorascluster.util.AVAILABLE_CHUNK_ROTATIONS
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.Player
import org.hibernate.HibernateException
import java.time.Duration
import java.util.*
import java.util.function.Consumer
import java.util.logging.Level


class LandService(private val pandorasClusterApi: PandorasClusterApi) {

    val landCache: LoadingCache<Chunk, Land> = Caffeine.newBuilder().maximumSize(10_000)
        .expireAfterAccess(Duration.ofMinutes(5))
        .expireAfterWrite(Duration.ofMinutes(5))
        .refreshAfterWrite(Duration.ofMinutes(1)).build { key -> getFullLand(key) }

    val unclaimedChunkCache: LoadingCache<Chunk, Boolean> = Caffeine.newBuilder().maximumSize(10_000)
        .expireAfterAccess(Duration.ofMinutes(5))
        .expireAfterWrite(Duration.ofMinutes(5))
        .refreshAfterWrite(Duration.ofMinutes(1)).build { key -> isChunkClaimed(key) }

    val showBorderOfLand: MutableList<Player> = mutableListOf()

    init {

        val pluginManager = pandorasClusterApi.getPlugin().server.pluginManager

        //Blocks
        pluginManager.registerEvents(LandBlockListener(pandorasClusterApi), pandorasClusterApi.getPlugin())

        //Entities
        pluginManager.registerEvents(LandEntityListener(pandorasClusterApi), pandorasClusterApi.getPlugin())
        pluginManager.registerEvents(
            LandHangingEntityListener(pandorasClusterApi, this),
            pandorasClusterApi.getPlugin()
        )
        pluginManager.registerEvents(LandVehicleListener(pandorasClusterApi), pandorasClusterApi.getPlugin())

        //Players
        pluginManager.registerEvents(LandPlayerInteractListener(pandorasClusterApi), pandorasClusterApi.getPlugin())
        pluginManager.registerEvents(PlayerConnectionListener(pandorasClusterApi), pandorasClusterApi.getPlugin())
        pluginManager.registerEvents(PlayerInteractEntityListener(pandorasClusterApi), pandorasClusterApi.getPlugin())
        pluginManager.registerEvents(PlayerLocationListener(pandorasClusterApi), pandorasClusterApi.getPlugin())

        //Misc
        pluginManager.registerEvents(
            LandContainerProtectionListener(pandorasClusterApi),
            pandorasClusterApi.getPlugin()
        )
        pluginManager.registerEvents(LandWorldListener(pandorasClusterApi), pandorasClusterApi.getPlugin())

        pandorasClusterApi.getPlugin().server.scheduler.runTaskTimerAsynchronously(
            pandorasClusterApi.getPlugin(),
            showBorder(),
            0L,
            pandorasClusterApi.getPlugin().config.getLong("tasks.show-border-multiplier") * 20L
        )
    }

    val space = 2.5
    val normalLandDustColor = DustOptions(Color.LIME, 2.5F)
    val otherLandDustColor = DustOptions(Color.RED, 2.5F)

    fun showBorder() = Runnable {

        showBorderOfLand.forEach { player ->

            val borderParticleDistance =
                (player.viewDistance*player.viewDistance) * pandorasClusterApi.getPlugin().config.getDouble("particle-border-max-distance")

            val y = player.eyeLocation.y.toInt()
            val land = getFullLand(player.chunk) ?: return@forEach
            val world = Bukkit.getWorld(land.world) ?: return@forEach
            val dustOptions = if (land.hasMemberAccess(player.uniqueId)) normalLandDustColor else otherLandDustColor

            val playerLocation = player.location
            val chunks = land.chunks.map { world.getChunkAt(it.chunkIndex) }
            for (chunk in chunks) {

                val chunkX = chunk.x
                val chunkZ = chunk.z

                val minX = chunkX * 16
                val minZ = chunkZ * 16

                val north = world.getChunkAt(chunkX, chunkZ - 1)
                val south = world.getChunkAt(chunkX, chunkZ + 1)
                val west = world.getChunkAt(chunkX - 1, chunkZ)
                val east = world.getChunkAt(chunkX + 1, chunkZ)

                if (!land.isMergedChunk(north)) {
                    for (x in minX until minX + 16) {
                        val location = world.getBlockAt(x, y, minZ).location
                        if (playerLocation.distanceSquared(location) < borderParticleDistance) {
                            player.spawnParticle(Particle.REDSTONE, location, 1, dustOptions)
                        }
                    }
                }

                if (!land.isMergedChunk(south)) {
                    for (x in minX until minX + 16) {
                        val location = world.getBlockAt(x, y, (minZ + 16)).location
                        if (playerLocation.distanceSquared(location) < borderParticleDistance) {
                            player.spawnParticle(Particle.REDSTONE, location, 1, dustOptions)
                        }
                    }
                }

                if (!land.isMergedChunk(west)) {
                    for (z in minZ until minZ + 16) {
                        val location = world.getBlockAt(minX, y, z).location
                        if (playerLocation.distanceSquared(location) < borderParticleDistance) {
                            player.spawnParticle(Particle.REDSTONE, location, 1, dustOptions)
                        }
                    }
                }

                if (!land.isMergedChunk(east)) {
                    for (z in minZ until minZ + 16) {
                        val location = world.getBlockAt((minX + 16), y, z).location
                        if (playerLocation.distanceSquared(location) < borderParticleDistance) {
                            player.spawnParticle(Particle.REDSTONE, location, 1, dustOptions)
                        }
                    }
                }
            }
        }
    }

    fun getLands(): List<Land> {
        var lands = emptyList<Land>()
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val query = session.createQuery("SELECT l FROM Land l", Land::class.java)
                lands = query.list()
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

        val claimed = unclaimedChunkCache.getIfPresent(chunk)
        if (claimed == true) return true

        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val query = session.createQuery(
                    "SELECT ch FROM ChunkPlaceholder ch WHERE chunkIndex = :chunkIndex",
                    ChunkPlaceholder::class.java
                )
                query.maxResults = 1
                query.setParameter("chunkIndex", chunk.chunkKey)
                return query.uniqueResult() != null
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Something went wrong!", e)
            Sentry.captureException(e)
            return false
        }
    }

    fun findConnectedChunk(player: Player, consumer: Consumer<Land?>) {
        val chunk = player.chunk
        var land: Land? = null
        for (facing in AVAILABLE_CHUNK_ROTATIONS) {
            val connectedChunk = player.world.getChunkAt(
                chunk.x + facing.modX,
                chunk.z + facing.modZ
            )

            val fullLand = getFullLand(connectedChunk)
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

        var land = landCache.getIfPresent(chunk)
        if (land != null) return land

        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val query = session.createQuery(
                    "SELECT ch FROM ChunkPlaceholder ch JOIN FETCH ch.land WHERE ch.chunkIndex = :chunkIndex",
                    ChunkPlaceholder::class.java
                )
                query.setParameter("chunkIndex", chunk.chunkKey)
                val chunkHolder = query.uniqueResult()
                if (chunkHolder != null) {
                    land = chunkHolder.land
                }

                if (land != null) {
                    landCache.put(chunk, land)
                }
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Could not find land", e)
            Sentry.captureException(e)
        }

        return land
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
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot load landmember $landPlayer", e)
            Sentry.captureException(e)
        }

        return null
    }

    fun checkWorldGuardRegion(chunk: Chunk): Boolean {

        if (!pandorasClusterApi.getPlugin().server.pluginManager.isPluginEnabled("WorldGuard")) return false
        val world = BukkitAdapter.adapt(chunk.world)
        val minChunkX = chunk.x shl 4
        val minChunkZ = chunk.z shl 4
        val maxChunkX = minChunkX + 15
        val maxChunkZ = minChunkZ + 15

        val regionManager = WorldGuard.getInstance().platform.regionContainer.get(world)
        val region = ProtectedCuboidRegion(
            "check_wg_overlaps",
            BlockVector3.at(minChunkX, chunk.world.minHeight, minChunkZ),
            BlockVector3.at(maxChunkX, chunk.world.maxHeight, maxChunkZ)
        )

        val regions = regionManager?.regions?.values
        return region.getIntersectingRegions(regions).isNotEmpty()
    }

    fun loadChunk(chunk: Chunk) {
        val land = getFullLand(chunk) ?: return
        landCache.put(chunk, land)
    }

    fun getChunksByLand(land: Land): Long {
        var chunks = 0L
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val query = session.createNativeQuery(
                    "SELECT COUNT(chunkIndex) FROM ChunkPlaceholder WHERE land_id = :landId",
                    Long::class.java
                )
                query.setParameter("landId", land.id)
                chunks = query.singleResult as Long
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot count chunks by land $land", e)
            Sentry.captureException(e)

        }

        return chunks
    }

    fun updateLoadedChunks(land: Land) {
        val world = Bukkit.getWorld(land.world) ?: return
        landCache.invalidateAll(land.chunks.map { world.getChunkAt(it.chunkIndex) })
    }

    fun claimChunk(chunk: Chunk) {
        unclaimedChunkCache.invalidate(chunk)
        landCache.invalidate(chunk)
    }

    fun toggleShowBorder(player: Player): Boolean {
        var currentState = showBorderOfLand.contains(player)
        if (currentState) {
            showBorderOfLand.remove(player)
        } else {
            showBorderOfLand.add(player)
        }
        currentState = showBorderOfLand.contains(player)
        return currentState
    }

    fun disableBorderView(player: Player) {
        showBorderOfLand.remove(player)
    }
}


private const val cannotUpdateLand = "Cannot update land"