package net.onelitefeather.pandorascluster.api

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.onelitefeather.pandorascluster.PandorasClusterPlugin
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.service.*
import org.bukkit.Chunk
import org.bukkit.entity.Player
import org.hibernate.SessionFactory
import java.util.*
import java.util.function.Consumer
import java.util.logging.Logger

class PandorasClusterApiImpl(private val plugin: PandorasClusterPlugin) : PandorasClusterApi {

    private var databaseService: DatabaseService? = null
    private var landService: LandService? = null
    private var databaseStorageService: DatabaseStorageService? = null
    private var landPlayerService: LandPlayerService? = null

    init {
        val config = plugin.config
        val jdbcUrl =
            config.getString("database.jdbcUrl", "'jdbc:mariadb://localhost:3306/pandorascluster?useSSL=false'")!!
        val databaseDriver = config.getString("database.driver", "org.mariadb.jdbc.Driver")!!
        val username = config.getString("database.username", "root")!!
        val password = config.getString("database.password", "%Schueler90")!!

        databaseService = DatabaseService(jdbcUrl, username, password, databaseDriver)
        databaseStorageService = DatabaseStorageService(this)
        landService = LandService(databaseStorageService!!, this)
        landPlayerService = LandPlayerService(this)
    }

    override fun getPlugin(): PandorasClusterPlugin {
        return plugin
    }

    override fun getDatabaseStorageService(): DatabaseStorageService {
        return databaseStorageService!!
    }

    override fun hasPlayerLand(player: Player): Boolean {
        return hasPlayerLand(player.uniqueId)
    }

    override fun hasPlayerLand(playerId: UUID): Boolean {
        return landService?.hasPlayerLand(playerId) ?: false
    }

    override fun isChunkClaimed(chunk: Chunk): Boolean {
        return landService?.isChunkClaimed(chunk) ?: false
    }

    override fun getLands(): List<Land> {
        return landService?.getLands() ?: listOf()
    }

    override fun getLands(player: Player): List<Land> {
        val lands: MutableList<Land> = ArrayList()
        for (landEntry in getLands()) {
            if (landEntry.owner?.getUniqueId() == player.uniqueId || landEntry.hasAccess(player.uniqueId)) {
                lands.add(landEntry)
            }
        }
        return lands
    }

    override fun getLandPlayer(player: Player): LandPlayer? {
        return landPlayerService?.getLandPlayer(player.uniqueId)
    }

    override fun getLandPlayer(uuid: UUID): LandPlayer? {
        return landPlayerService?.getLandPlayer(uuid)
    }

    override fun getLandPlayer(name: String): LandPlayer? {
        return landPlayerService?.getLandPlayer(name)
    }

    override fun getSessionFactory(): SessionFactory {
        return getDatabaseService().sessionFactory
    }

    override fun getLandPlayerService(): LandPlayerService {
        return landPlayerService!!
    }

    override fun getDatabaseService(): DatabaseService {
        return databaseService!!
    }

    override fun getLandService(): LandService {
        return landService!!
    }

    override fun getLogger(): Logger {
        return plugin.logger
    }

    override fun translateLegacyCodes(text: String): Component {
        return MiniMessage.miniMessage().deserialize(
            MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(text))
        )
    }

    override fun getLand(chunk: Chunk): Land? {
        return landService?.getFullLand(chunk)
    }

    override fun registerPlayer(uuid: UUID, name: String, consumer: Consumer<Boolean>) {
        landPlayerService?.createPlayer(uuid, name, consumer)
    }
}