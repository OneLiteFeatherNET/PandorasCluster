package net.onelitefeather.pandorascluster

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.extra.confirmation.CommandConfirmationManager
import cloud.commandframework.meta.CommandMeta
import cloud.commandframework.minecraft.extras.MinecraftHelp
import cloud.commandframework.paper.PaperCommandManager
import io.sentry.Sentry
import io.sentry.jul.SentryHandler
import io.sentry.log4j2.SentryAppender
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.PandorasClusterApiImpl
import net.onelitefeather.pandorascluster.command.commands.*
import net.onelitefeather.pandorascluster.command.parser.LandFlagParser
import net.onelitefeather.pandorascluster.command.parser.LandPlayerParser
import net.onelitefeather.pandorascluster.extensions.buildCommandSystem
import net.onelitefeather.pandorascluster.extensions.buildHelpSystem
import net.onelitefeather.pandorascluster.extensions.sentry
import net.onelitefeather.pandorascluster.listener.PlayerConnectionListener
import org.bukkit.command.CommandSender
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

class PandorasClusterPlugin : JavaPlugin() {

    lateinit var paperCommandManager: PaperCommandManager<CommandSender>
    lateinit var annotationParser: AnnotationParser<CommandSender>
    lateinit var minecraftHelp: MinecraftHelp<CommandSender>
    lateinit var confirmationManager: CommandConfirmationManager<CommandSender>

    lateinit var bukkitAudiences: BukkitAudiences
    lateinit var api: PandorasClusterApiImpl

    override fun onLoad() {
        sentry()
    }

    override fun onEnable() {
        try {

            saveDefaultConfig()

            bukkitAudiences = BukkitAudiences.create(this)
            api = PandorasClusterApiImpl(this)
            server.servicesManager.register(PandorasClusterApi::class.java, api, this, ServicePriority.Highest)

            val pluginManager = server.pluginManager
            pluginManager.registerEvents(PlayerConnectionListener(api), this)

            buildCommandSystem()
            registerCommands()
            buildHelpSystem()
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }

    private fun registerCommands() {

        annotationParser.parse(LandPlayerParser(api))
        annotationParser.parse(LandFlagParser(api))

        annotationParser.parse(LandTeleportCommands(api))
        annotationParser.parse(SetHomeCommand(api))
        annotationParser.parse(SetFlagCommand(api))
        annotationParser.parse(ClaimCommand(api))
        annotationParser.parse(SetOwnerCommand(api))
        annotationParser.parse(SetRoleCommand(api))
        annotationParser.parse(LandRemovePlayerCommand(api))
        annotationParser.parse(LandInfoCommand(api))

        val builder = paperCommandManager.commandBuilder("land")
        paperCommandManager.command(builder.literal("confirm").
        meta(CommandMeta.DESCRIPTION, "Confirm").handler(confirmationManager.createConfirmationExecutionHandler()))


    }

    override fun onDisable() {
        api.getDatabaseService().shutdown()
        server.servicesManager.unregisterAll(this)
    }
}