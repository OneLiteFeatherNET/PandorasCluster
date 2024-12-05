package net.onelitefeather.pandorascluster.service

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
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.onelitefeather.pandorascluster.PandorasClusterPlugin
import net.onelitefeather.pandorascluster.command.commands.*
import net.onelitefeather.pandorascluster.command.parser.LandFlagParser
import net.onelitefeather.pandorascluster.command.parser.LandPlayerParser
import net.onelitefeather.pandorascluster.util.PLUGIN_PREFIX
import org.bukkit.command.CommandSender
import java.util.concurrent.TimeUnit
import java.util.function.Function

class PaperCommandService(private val plugin: PandorasClusterPlugin) {

    lateinit var paperCommandManager: PaperCommandManager<CommandSender>
    lateinit var annotationParser: AnnotationParser<CommandSender>
    lateinit var minecraftHelp: MinecraftHelp<CommandSender>
    lateinit var confirmationManager: CommandConfirmationManager<CommandSender>

    fun setup() {
        paperCommandManager = buildCommandManager()
        annotationParser = buildAnnotationParser()
        confirmationManager = buildConfirmnationManager()
        minecraftHelp = buildHelpSystem()
        registerCommands()
    }

    fun buildCommandManager(): PaperCommandManager<CommandSender> {

        val commandManager = PaperCommandManager.createNative(plugin, CommandExecutionCoordinator.simpleCoordinator())
        if (commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            commandManager.registerBrigadier()
            plugin.logger.info("Brigadier support enabled")
        }

        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions()
            plugin.logger.info("Asynchronous completions enabled")
        }

        return commandManager
    }

    private fun buildAnnotationParser(): AnnotationParser<CommandSender> {
        val commandMetaFunction = Function<ParserParameters, CommandMeta> { p: ParserParameters ->
            CommandMeta.simple().with(
                CommandMeta.DESCRIPTION,
                p.get(StandardParameters.DESCRIPTION, "No description")
            ).build()
        }
        return AnnotationParser(paperCommandManager, CommandSender::class.java, commandMetaFunction)
    }

    private fun buildConfirmnationManager(): CommandConfirmationManager<CommandSender> {
        val commandConfirmationManager = CommandConfirmationManager(
            20L, TimeUnit.SECONDS, { context: CommandPostprocessingContext<CommandSender> ->
                plugin.bukkitAudiences.sender(context.commandContext.sender).sendMessage(
                    Component.translatable("command.confirm").arguments(PLUGIN_PREFIX)
                )
            },
            { sender: CommandSender ->
                plugin.bukkitAudiences.sender(sender).sendMessage(
                    Component.translatable("command.confirm.no-pending-commands").arguments(PLUGIN_PREFIX)
                )
            }
        )

        commandConfirmationManager.registerConfirmationProcessor(paperCommandManager)
        return commandConfirmationManager
    }

    private fun buildHelpSystem(): MinecraftHelp<CommandSender> {
        val help = MinecraftHelp.createNative("/pandorascluster help", paperCommandManager)
        help.helpColors = MinecraftHelp.HelpColors.of(
            NamedTextColor.DARK_GREEN,
            NamedTextColor.GREEN,
            NamedTextColor.BLUE,
            NamedTextColor.DARK_BLUE,
            NamedTextColor.AQUA
        )
        return help
    }

    private fun registerCommands() {

        annotationParser.parse(LandPlayerParser(plugin.api))
        annotationParser.parse(LandFlagParser(plugin.api))

        annotationParser.parse(LandTeleportCommands(plugin.api))
        annotationParser.parse(LandToggleBorderCommand(plugin.api, plugin))
        annotationParser.parse(SetHomeCommand(plugin.api))
        annotationParser.parse(SetFlagCommand(plugin.api))
        annotationParser.parse(ClaimCommand(plugin.api, plugin))
        annotationParser.parse(UnclaimCommand(plugin.api))
        annotationParser.parse(SetOwnerCommand(plugin.api))
        annotationParser.parse(SetRoleCommand(plugin.api))
        annotationParser.parse(LandInfoCommand(plugin.api))

        val builder = paperCommandManager.commandBuilder("land")
        paperCommandManager.command(
            builder.literal("confirm").meta(CommandMeta.DESCRIPTION, "Confirm")
                .handler(confirmationManager.createConfirmationExecutionHandler())
        )
    }
}