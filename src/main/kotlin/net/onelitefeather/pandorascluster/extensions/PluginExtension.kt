package net.onelitefeather.pandorascluster.extensions

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.arguments.parser.ParserParameters
import cloud.commandframework.arguments.parser.StandardParameters
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.execution.postprocessor.CommandPostprocessingContext
import cloud.commandframework.extra.confirmation.CommandConfirmationManager
import cloud.commandframework.meta.CommandMeta
import cloud.commandframework.minecraft.extras.MinecraftHelp
import cloud.commandframework.paper.PaperCommandManager
import io.sentry.Sentry
import net.kyori.adventure.text.format.NamedTextColor
import net.onelitefeather.pandorascluster.PandorasClusterPlugin
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.TimeUnit
import java.util.function.Function
import java.util.logging.Level

fun PandorasClusterPlugin.buildCommandSystem() {
    try {
        paperCommandManager = PaperCommandManager.createNative(this, CommandExecutionCoordinator.simpleCoordinator())
    } catch (e: Exception) {
        logger.log(Level.WARNING, "Failed to build command system", e)
        Sentry.captureException(e)
        server.pluginManager.disablePlugin(this)
        return
    }

    if (paperCommandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
        paperCommandManager.registerBrigadier()
        logger.info("Brigadier support enabled")
    }

    if (paperCommandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
        paperCommandManager.registerAsynchronousCompletions()
        logger.info("Asynchronous completions enabled")
    }

    confirmationManager = CommandConfirmationManager(
        20L, TimeUnit.SECONDS, { context: CommandPostprocessingContext<CommandSender> ->
            bukkitAudiences.sender(context.commandContext.sender).sendMessage(miniMessage { "<lang:command.confirm:'${api.pluginPrefix()}'>" })
        },
        { sender: CommandSender ->
            bukkitAudiences.sender(sender).sendMessage(miniMessage { "<lang:command.confirm.no-pending-commands:'${api.pluginPrefix()}'>" })
        }
    )

    confirmationManager.registerConfirmationProcessor(paperCommandManager)

    val commandMetaFunction =
        Function<ParserParameters, CommandMeta> { p: ParserParameters ->
            CommandMeta.simple().with(
                CommandMeta.DESCRIPTION,
                p.get(StandardParameters.DESCRIPTION, "No description")
            ).build()
        }

    annotationParser = AnnotationParser(paperCommandManager, CommandSender::class.java, commandMetaFunction)
}

fun PandorasClusterPlugin.buildHelpSystem() {
    minecraftHelp = MinecraftHelp.createNative(
        "/pandorascluster help",
        paperCommandManager
    )

    minecraftHelp.helpColors = MinecraftHelp.HelpColors.of(
        NamedTextColor.DARK_GREEN,
        NamedTextColor.GREEN,
        NamedTextColor.BLUE,
        NamedTextColor.DARK_BLUE,
        NamedTextColor.AQUA
    )
}

fun JavaPlugin.sentry() {
    val dsn = System.getProperty("sentry.dsn", "https://b798943d3b7f4bb0a3b0c0ac14cfd376@sentry.themeinerlp.dev/3")
    val env = System.getProperty("sentry.env", "local")
    Sentry.init {
        it.dsn = dsn
        it.environment = env
        it.release = this.description.version
    }
}
