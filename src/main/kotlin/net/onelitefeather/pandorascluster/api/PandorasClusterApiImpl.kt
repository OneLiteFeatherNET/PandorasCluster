package net.onelitefeather.pandorascluster.api

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.onelitefeather.pandorascluster.PandorasClusterPlugin
import net.onelitefeather.pandorascluster.extensions.miniMessage
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.service.DatabaseService
import net.onelitefeather.pandorascluster.service.DatabaseStorageService
import net.onelitefeather.pandorascluster.service.LandPlayerService
import net.onelitefeather.pandorascluster.service.LandService
import org.bukkit.Chunk
import org.bukkit.entity.Player
import org.hibernate.SessionFactory
import java.util.*
import java.util.function.Consumer
import java.util.logging.Logger

class PandorasClusterApiImpl(private val plugin: PandorasClusterPlugin) : PandorasClusterApi {

    private lateinit var databaseService: DatabaseService
    private lateinit var landService: LandService
    private lateinit var databaseStorageService: DatabaseStorageService
    private lateinit var landPlayerService: LandPlayerService

    init {

        val jdbcUrl = plugin.config.getString("database.jdbcUrl")
        val databaseDriver = plugin.config.getString("database.driver")
        val username = plugin.config.getString("database.username")
        val password = plugin.config.getString("database.password")

        if (jdbcUrl != null && databaseDriver != null && username != null && password != null) {
            databaseService = DatabaseService(jdbcUrl, username, password, databaseDriver)
            databaseStorageService = DatabaseStorageService(this)
            landService = LandService(this)
            landPlayerService = LandPlayerService(this)
        } else {
            this.plugin.server.pluginManager.disablePlugin(plugin)
        }
    }

    override fun getPlugin(): PandorasClusterPlugin {
        return plugin
    }

    override fun getDatabaseStorageService(): DatabaseStorageService {
        return databaseStorageService
    }

    override fun hasPlayerLand(player: Player): Boolean {
        return hasPlayerLand(player.uniqueId)
    }

    override fun hasPlayerLand(playerId: UUID): Boolean {
        return landService.hasPlayerLand(playerId)
    }

    override fun isChunkClaimed(chunk: Chunk): Boolean {
        return landService.isChunkClaimed(chunk)
    }

    override fun getLands(): List<Land> {
        return landService.getLands()
    }

    override fun getLands(player: Player): List<Land> {
        return getLands().filter { it.owner?.getUniqueId() == player.uniqueId || it.hasAccess(player.uniqueId) }
    }

    override fun getLandPlayer(player: Player): LandPlayer? {
        return landPlayerService.getLandPlayer(player.uniqueId)
    }

    override fun getLandPlayer(uuid: UUID): LandPlayer? {
        return landPlayerService.getLandPlayer(uuid)
    }

    override fun getLandPlayer(name: String): LandPlayer? {
        return landPlayerService.getLandPlayer(name)
    }

    override fun getSessionFactory(): SessionFactory {
        return getDatabaseService().sessionFactory
    }

    override fun getLandPlayerService(): LandPlayerService {
        return landPlayerService
    }

    override fun getDatabaseService(): DatabaseService {
        return databaseService
    }

    override fun getLandService(): LandService {
        return landService
    }

    override fun getLogger(): Logger {
        return plugin.logger
    }

    override fun translateLegacyCodes(text: String): Component {
        return miniMessage {
            MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(text))
        }
    }

    override fun getLand(chunk: Chunk): Land? {
        return landService.getFullLand(chunk)
    }

    override fun registerPlayer(uuid: UUID, name: String, consumer: Consumer<Boolean>) {
        landPlayerService.createPlayer(uuid, name, consumer)
    }
}
