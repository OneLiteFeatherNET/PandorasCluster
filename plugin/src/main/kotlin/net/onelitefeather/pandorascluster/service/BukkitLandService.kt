package net.onelitefeather.pandorascluster.service

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion
import net.onelitefeather.pandorascluster.PandorasClusterPlugin
import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.listener.LandContainerProtectionListener
import net.onelitefeather.pandorascluster.listener.LandWorldListener
import net.onelitefeather.pandorascluster.listener.block.LandBlockListener
import net.onelitefeather.pandorascluster.listener.entity.LandEntityDamageListener
import net.onelitefeather.pandorascluster.listener.entity.LandEntityListener
import net.onelitefeather.pandorascluster.listener.entity.LandHangingEntityListener
import net.onelitefeather.pandorascluster.listener.entity.LandVehicleListener
import net.onelitefeather.pandorascluster.listener.player.LandPlayerInteractListener
import net.onelitefeather.pandorascluster.listener.player.PlayerConnectionListener
import net.onelitefeather.pandorascluster.listener.player.PlayerInteractEntityListener
import net.onelitefeather.pandorascluster.listener.player.PlayerLocationListener
import net.onelitefeather.pandorascluster.util.AVAILABLE_CHUNK_ROTATIONS
import net.onelitefeather.pandorascluster.util.DEFAULT_PARTICLE_DATA
import net.onelitefeather.pandorascluster.util.ParticleData
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval
import java.util.function.Consumer

class BukkitLandService(
    private val pandorasClusterApi: PandorasClusterApi,
    private val plugin: PandorasClusterPlugin
) : ChunkUtils {

    val showBorderOfLand: MutableList<Player> = mutableListOf()
    var particleData: ParticleData = DEFAULT_PARTICLE_DATA

    init {
        registerListeners()
        particleData = buildParticleData()
        startBorderTask()
    }

    fun findConnectedChunk(player: Player, consumer: Consumer<Land?>) {
        val chunk = player.chunk
        var land: Land? = null
        for (facing in AVAILABLE_CHUNK_ROTATIONS) {
            val connectedChunk = player.world.getChunkAt(
                chunk.x + facing.modX,
                chunk.z + facing.modZ
            )

            val fullLand = pandorasClusterApi.getLandService().getLand(connectedChunk.chunkKey)
            if (fullLand != null) {
                land = fullLand
            }
        }

        consumer.accept(land)
    }

    @ScheduledForRemoval(inVersion = "1.2.2")
    @Deprecated(message = "This method will be removed in future releases")
    fun checkWorldGuardRegion(chunk: Chunk): Boolean {

        if (!plugin.server.pluginManager.isPluginEnabled("WorldGuard")) return false
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

    /**
     * Toggle the chunk particle border
     * @param player the player
     */
    fun toggleShowBorder(player: Player): Boolean {
        val currentState = showBorderOfLand.contains(player)
        if (currentState) {
            showBorderOfLand.remove(player)
            return false
        } else {
            showBorderOfLand.add(player)
            return true
        }
    }

    private fun spawnParticle(player: Player, trusted: Boolean, location: Location) {
        val radius = particleData.radius * particleData.radius
        if (player.location.distanceSquared(location) < radius) {
            val sectionKey =
                if (trusted) "particle-data.trusted-particle-data" else "particle-data.untrusted-particle-data"

            val particle = if (trusted) particleData.trustedParticle else particleData.untrustedParticle
            val section = plugin.config.getConfigurationSection(sectionKey)
            val data = if (section != null) particleData.getExtraData(trusted, section) else null
            player.spawnParticle(
                particle,
                location,
                1,
                particleData.offX,
                particleData.offY,
                particleData.offZ,
                particleData.getSpeed(trusted),
                data
            )
        }
    }

    private fun showBorder() = Runnable {

        showBorderOfLand.forEach { player ->

            val land = pandorasClusterApi.getLandService().getLand(player.chunk.chunkKey) ?: return@forEach
            val world = Bukkit.getWorld(land.world) ?: return@forEach
            val access = land.hasMemberAccess(player.uniqueId)

            val playerLocation = player.location
            val chunks = land.chunks.map { world.getChunkAt(it.chunkIndex) }
            for (chunk in chunks) {

                val chunkX = chunk.x
                val chunkZ = chunk.z

                val minX = chunkX * chunkLength()
                val minZ = chunkZ * chunkLength()

                val north = world.getChunkAt(chunkX, chunkZ - 1)
                val south = world.getChunkAt(chunkX, chunkZ + 1)
                val west = world.getChunkAt(chunkX - 1, chunkZ)
                val east = world.getChunkAt(chunkX + 1, chunkZ)

                if (!land.isChunkMerged(north.chunkKey)) {
                    (minX until minX + chunkLength())
                        .asSequence()
                        .map { world.getBlockAt(it, playerLocation.blockY, minZ).location }
                        .forEach { spawnParticle(player, access, it) }
                }

                if (!land.isChunkMerged(south.chunkKey)) {
                    (minX until minX + chunkLength())
                        .asSequence()
                        .map { world.getBlockAt(it, playerLocation.blockY, (minZ + chunkLength())).location }
                        .forEach { spawnParticle(player, access, it) }
                }

                if (!land.isChunkMerged(west.chunkKey)) {
                    (minZ until minZ + chunkLength())
                        .asSequence()
                        .map { world.getBlockAt(minX, playerLocation.blockY, it).location }
                        .forEach { spawnParticle(player, access, it) }
                }

                if (!land.isChunkMerged(east.chunkKey)) {
                    (minZ until minZ + chunkLength())
                        .asSequence()
                        .map { world.getBlockAt((minX + chunkLength()), playerLocation.blockY, it).location }
                        .forEach { spawnParticle(player, access, it) }
                }
            }
        }
    }

    private fun registerListeners() {
        val pluginManager = plugin.server.pluginManager

        //Blocks
        pluginManager.registerEvents(LandBlockListener(pandorasClusterApi), plugin)

        //Entities
        pluginManager.registerEvents(LandEntityListener(pandorasClusterApi, plugin), plugin)
        pluginManager.registerEvents(LandHangingEntityListener(pandorasClusterApi, this), plugin)
        pluginManager.registerEvents(LandVehicleListener(pandorasClusterApi), plugin)
        pluginManager.registerEvents(LandEntityDamageListener(pandorasClusterApi), plugin)

        //Players
        pluginManager.registerEvents(LandPlayerInteractListener(pandorasClusterApi), plugin)
        pluginManager.registerEvents(PlayerConnectionListener(pandorasClusterApi, plugin), plugin)
        pluginManager.registerEvents(PlayerInteractEntityListener(pandorasClusterApi), plugin)
        pluginManager.registerEvents(PlayerLocationListener(pandorasClusterApi), plugin)

        //Misc
        pluginManager.registerEvents(LandContainerProtectionListener(pandorasClusterApi), plugin)
        pluginManager.registerEvents(LandWorldListener(pandorasClusterApi), plugin)
    }

    private fun startBorderTask() {
        val showBorderMultiplier = plugin.config.getLong("tasks.show-border-multiplier")
        plugin.server.scheduler.runTaskTimerAsynchronously(plugin, showBorder(), 0L, showBorderMultiplier * 20L)
    }

    private fun buildParticleData(): ParticleData {
        val config = plugin.config
        return ParticleData(
            Particle.valueOf(config.getString("trusted-particle", "FLAME")?.uppercase()!!),
            Particle.valueOf(config.getString("untrusted-particle", "FLAME")?.uppercase()!!),
            config.getInt("particle-data.radius"),
            config.getDouble("particle-data.trusted-speed"),
            config.getDouble("particle-data.untrusted-speed"),
            config.getDouble("particle-data.offX"),
            config.getDouble("particle-data.offY"),
            config.getDouble("particle-data.offZ"),
            null
        )
    }
}