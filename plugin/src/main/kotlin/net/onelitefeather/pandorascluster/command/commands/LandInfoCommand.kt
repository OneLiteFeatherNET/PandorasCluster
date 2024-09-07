package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.LandRole
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.api.models.database.player.LandMember
import org.bukkit.entity.Player

class LandInfoCommand(private val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land info")
    @CommandDescription("Get information about the land you standing on")
    @CommandPermission("pandorascluster.command.land.info")
    fun execute(player: Player) {

        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val land = pandorasClusterApi.getLand(player.chunk)
        if (land == null) {
            player.sendMessage(Component.translatable("chunk-already-claimed").arguments(pluginPrefix))
            return
        }

        val accessMessage = if (land.hasAccess(player.uniqueId))
            Component.translatable("boolean-true") else Component.translatable("boolean-false")

        player.sendMessage(Component.translatable("command.info.owner").arguments(pluginPrefix,
            Component.text(land.owner?.name ?: player.name)))

        player.sendMessage(Component.translatable("command.info.access").arguments(pluginPrefix, accessMessage))
        player.sendMessage(Component.translatable("command.info.members").arguments(pandorasClusterApi.pluginPrefix(), buildMembers(land)))

        player.sendMessage(Component.translatable("command.info.members.banned").arguments(
            pandorasClusterApi.pluginPrefix(), buildDeniedMembers(land)))

        player.sendMessage(Component.translatable("command.info.flags").arguments(pandorasClusterApi.pluginPrefix(), buildFlags(land)))
        player.sendMessage(Component.translatable("command.info.total-chunk-count").arguments(
            pluginPrefix,
            Component.text(pandorasClusterApi.getLandService().getChunksByLand(land))))
    }

    private fun buildDeniedMembers(land: Land): Component {

       val members = land.landMembers
            .filter(this::filterDeniedMembers)
            .filterNot(this::filterPlayerNameNotNull).map {
                Component.translatable("command.info.members.entry").arguments(
                    MiniMessage.miniMessage().deserialize(it.role.display),
                    Component.text(it.member?.name!!))
            }.toList()

        return if (members.isNotEmpty())
            Component.join(JoinConfiguration.separator(Component.text(", ")), members) else
            Component.translatable("command.info.members.nobody")
    }

    private fun buildMembers(land: Land): Component {
        val members =
            land.landMembers.filterNot(this::filterDeniedMembers).filterNot(this::filterPlayerNameNotNull).map {
                Component.translatable("command.info.members.entry").arguments(
                    MiniMessage.miniMessage().deserialize(it.role.display),
                    Component.text(it.member?.name!!))
            }.toList()

        return if (members.isNotEmpty())
            Component.join(JoinConfiguration.separator(Component.text(", ")), members) else
            Component.translatable("command.info.members.nobody")
    }

    private fun buildFlags(land: Land): Component {

        val flags = land.flags.map {
            val value = it.value ?: "Unknown Value"
            val flagName = it.name ?: "Unknown Flag"

            val booleanFlag = it.type?.toInt() == 2

            val symbolColor = if (booleanFlag) {
                val booleanValue = value.toBoolean()
                if (booleanValue) {
                    Component.translatable("command.info.flag.enabled")
                } else {
                    Component.translatable("command.info.flag.disabled")
                }
            } else {
                Component.translatable("command.info.flag.disabled")
            }

            val suggestionValue = if (booleanFlag) !value.toBoolean() else value
            Component.translatable("command.info.flags.entry").arguments(
                Component.text(flagName),
                Component.text(value),
                Component.text(flagName),
                Component.text(suggestionValue.toString()),
                symbolColor
            )
        }.toList()

        return if (flags.isNotEmpty()) Component.join(JoinConfiguration.noSeparators(), flags) else
            Component.translatable("command.info.flags.none")
    }

    private fun filterDeniedMembers(landMember: LandMember): Boolean = landMember.role == LandRole.BANNED

    private fun filterPlayerNameNotNull(landMember: LandMember) = landMember.member?.name == null
}
