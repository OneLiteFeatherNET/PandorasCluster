package net.onelitefeather.pandorascluster

import net.kyori.adventure.key.Key
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import net.kyori.adventure.util.UTF8ResourceBundleControl
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.PandorasClusterApiImpl
import net.onelitefeather.pandorascluster.notification.DiscordStaffNotification
import net.onelitefeather.pandorascluster.notification.MinecraftStaffNotification
import net.onelitefeather.pandorascluster.service.BukkitLandService
import net.onelitefeather.pandorascluster.service.PaperCommandService
import net.onelitefeather.pandorascluster.translation.PluginTranslationRegistry
import net.onelitefeather.pandorascluster.util.discord.DiscordWebhook
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class PandorasClusterPlugin : JavaPlugin() {

    private val supportedLocals: Array<Locale> = arrayOf(Locale.US, Locale.GERMAN)
    lateinit var api: PandorasClusterApiImpl
    lateinit var bukkitAudiences: BukkitAudiences
    lateinit var bukkitLandService: BukkitLandService
    private lateinit var paperCommandService: PaperCommandService

    override fun onEnable() {

        saveDefaultConfig()
        config.options().copyDefaults(true)
        saveConfig()

        bukkitAudiences = BukkitAudiences.create(this)

        api = PandorasClusterApiImpl()
        server.servicesManager.register(PandorasClusterApi::class.java, api, this, ServicePriority.Highest)

        bukkitLandService = BukkitLandService(api, this)

        paperCommandService = PaperCommandService(this)
        paperCommandService.setup()

        val registry = TranslationRegistry.create(Key.key("pandorascluster", "localization"))
        supportedLocals.forEach { locale ->
            val bundle = ResourceBundle.getBundle("pandorascluster", locale, UTF8ResourceBundleControl.get())
            registry.registerAll(locale, bundle, false)
        }
        registry.defaultLocale(supportedLocals.first())
        GlobalTranslator.translator().addSource(PluginTranslationRegistry(registry))
    }

    override fun onDisable() {
        if(this::api.isInitialized){
            api.getDatabaseService().shutdown()
            server.servicesManager.unregisterAll(this)
        }
    }

    private fun addStaffNotifications() {
        api.getStaffNotification().addStaffNotification(MinecraftStaffNotification(api, this))
        val discordWebhook = buildDiscordWebhook()
        if(discordWebhook != null) {
            api.getStaffNotification().addStaffNotification(DiscordStaffNotification(api, discordWebhook))
        }
    }

    private fun buildDiscordWebhook(): DiscordWebhook? {
        val useDiscordStaffNotification = config.getBoolean("staff.notification.discord.enabled")
        val token = config.getString("staff.notification.discord.token", "")!!
        val tokenId = config.getString("staff.notification.discord.tokenId", "")!!

        if (!useDiscordStaffNotification || token.isEmpty() || tokenId.isEmpty()) return null

        return DiscordWebhook(token, tokenId)
    }
}
