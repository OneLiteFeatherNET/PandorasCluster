package net.onelitefeather.pandorascluster

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.extra.confirmation.CommandConfirmationManager
import cloud.commandframework.meta.CommandMeta
import cloud.commandframework.minecraft.extras.MinecraftHelp
import cloud.commandframework.paper.PaperCommandManager

import net.kyori.adventure.key.Key
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import net.kyori.adventure.util.UTF8ResourceBundleControl
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.PandorasClusterApiImpl
import net.onelitefeather.pandorascluster.command.commands.*
import net.onelitefeather.pandorascluster.command.parser.LandFlagParser
import net.onelitefeather.pandorascluster.command.parser.LandPlayerParser
import net.onelitefeather.pandorascluster.extensions.buildCommandSystem
import net.onelitefeather.pandorascluster.extensions.buildHelpSystem
import net.onelitefeather.pandorascluster.translation.PluginTranslationRegistry
import org.bukkit.command.CommandSender
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class PandorasClusterPlugin : JavaPlugin() {

    private val supportedLocals: Array<Locale> = arrayOf(Locale.US, Locale.GERMAN)
    lateinit var paperCommandManager: PaperCommandManager<CommandSender>
    lateinit var annotationParser: AnnotationParser<CommandSender>
    lateinit var minecraftHelp: MinecraftHelp<CommandSender>
    lateinit var confirmationManager: CommandConfirmationManager<CommandSender>

    lateinit var bukkitAudiences: BukkitAudiences
    lateinit var api: PandorasClusterApiImpl

    override fun onEnable() {
        saveDefaultConfig()
        config.options().copyDefaults(true)
        saveConfig()

        bukkitAudiences = BukkitAudiences.create(this)
        api = PandorasClusterApiImpl(this)
        server.servicesManager.register(PandorasClusterApi::class.java, api, this, ServicePriority.Highest)

        buildCommandSystem()
        registerCommands()
        buildHelpSystem()

        val registry = TranslationRegistry.create(Key.key("pandorascluster", "localization"))
        supportedLocals.forEach { locale ->
            val bundle = ResourceBundle.getBundle("pandorascluster", locale, UTF8ResourceBundleControl.get())
            registry.registerAll(locale, bundle, false)
        }
        registry.defaultLocale(supportedLocals.first())
        GlobalTranslator.translator().addSource(PluginTranslationRegistry(registry))
    }

    private fun registerCommands() {

        annotationParser.parse(LandPlayerParser(api))
        annotationParser.parse(LandFlagParser(api))

        annotationParser.parse(LandTeleportCommands(api))
        annotationParser.parse(LandToggleBorderCommand(api))
        annotationParser.parse(SetHomeCommand(api))
        annotationParser.parse(SetFlagCommand(api))
        annotationParser.parse(ClaimCommand(api))
        annotationParser.parse(UnclaimCommand(api))
        annotationParser.parse(SetOwnerCommand(api))
        annotationParser.parse(SetRoleCommand(api))
        annotationParser.parse(LandInfoCommand(api))

        val builder = paperCommandManager.commandBuilder("land")
        paperCommandManager.command(builder.literal("confirm").
        meta(CommandMeta.DESCRIPTION, "Confirm").handler(confirmationManager.createConfirmationExecutionHandler()))
    }

    override fun onDisable() {
        if(this::api.isInitialized){
            api.getDatabaseService().shutdown()
            server.servicesManager.unregisterAll(this)
        }
    }
}
