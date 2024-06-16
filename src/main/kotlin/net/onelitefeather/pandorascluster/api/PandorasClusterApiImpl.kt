package net.onelitefeather.pandorascluster.api

import net.kyori.adventure.text.Component
import net.kyori.adventure.util.UTF8ResourceBundleControl
import net.onelitefeather.pandorascluster.PandorasClusterPlugin
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.notification.DiscordStaffNotification
import net.onelitefeather.pandorascluster.notification.MinecraftStaffNotification
import net.onelitefeather.pandorascluster.notification.StaffNotification
import net.onelitefeather.pandorascluster.service.DatabaseService
import net.onelitefeather.pandorascluster.service.DatabaseStorageService
import net.onelitefeather.pandorascluster.service.LandPlayerService
import net.onelitefeather.pandorascluster.service.LandService
import net.onelitefeather.pandorascluster.util.discord.DiscordWebhook
import org.bukkit.Chunk
import org.bukkit.entity.Player
import org.hibernate.SessionFactory
import java.util.*
import java.util.logging.Logger

class PandorasClusterApiImpl(private val plugin: PandorasClusterPlugin) : PandorasClusterApi {

    private lateinit var databaseService: DatabaseService
    private lateinit var landService: LandService
    private lateinit var databaseStorageService: DatabaseStorageService
    private lateinit var landPlayerService: LandPlayerService
    private lateinit var staffNotification: StaffNotification
    private var messages: ResourceBundle

    init {

        val jdbcUrl = plugin.config.getString("database.jdbcUrl")
        val databaseDriver = plugin.config.getString("database.driver")
        val username = plugin.config.getString("database.username")
        val password = plugin.config.getString("database.password")

        messages = ResourceBundle.getBundle("pandorascluster", Locale.US, UTF8ResourceBundleControl())

        if (jdbcUrl != null && databaseDriver != null && username != null && password != null) {
            databaseService = DatabaseService(plugin, jdbcUrl, username, password, databaseDriver)
            if (databaseService.isRunning()) {
                databaseStorageService = DatabaseStorageService(this)
                landService = LandService(this)
                landPlayerService = LandPlayerService(this)

                val useDiscordStaffNotification = plugin.config.getBoolean("staff.notification.discord.enabled")

                staffNotification = if (useDiscordStaffNotification) {

                    val webhook = DiscordWebhook(
                        plugin.config.getString("staff.notification.discord.token")!!,
                        plugin.config.getString("staff.notification.discord.tokenId")!!
                    )

                    DiscordStaffNotification(this, webhook)
                } else {
                    MinecraftStaffNotification(this)
                }
            }
        } else {
            this.plugin.server.pluginManager.disablePlugin(plugin)
        }
    }

    override fun pluginPrefix(): Component {
        return Component.translatable("prefix")
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

    override fun unclaimLand(player: Player) {
        databaseStorageService.unclaimLand(player)
    }

    override fun getLands(): List<Land> {
        return landService.getLands()
    }

    override fun getLands(player: Player): List<Land> {
        return getLands().filter { it.owner?.getUniqueId() == player.uniqueId || it.hasAccess(player.uniqueId) }
    }

    override fun getLandPlayer(player: Player): LandPlayer? {
        return getLandPlayer(player.uniqueId)
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

    override fun getLand(chunk: Chunk): Land? {
        return landService.getFullLand(chunk)
    }

    override fun getLand(landOwner: LandPlayer): Land? {
        return landService.getLand(landOwner)
    }

    override fun registerPlayer(uuid: UUID, name: String): Boolean {
        return landPlayerService.createPlayer(uuid, name)
    }

    override fun getStaffNotificaton() = staffNotification
}