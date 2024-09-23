package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.api.player.LandMember
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import org.bukkit.entity.Player

class LandInfoCommand(private val pandorasClusterApi: PandorasClusterApi) : ChunkUtils {

    @CommandMethod("land info")
    @CommandDescription("Get information about the land you standing on")
    @CommandPermission("pandorascluster.command.land.info")
    fun execute(player: Player) {

        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(player.chunk))
        if (land == null) {
            player.sendMessage(Component.translatable("chunk-already-claimed").arguments(pluginPrefix))
            return
        }

        val accessMessage = if (land.hasMemberAccess(player.uniqueId))
            Component.translatable("boolean-true") else Component.translatable("boolean-false")

        player.sendMessage(Component.translatable("command.info.owner").arguments(pluginPrefix,
            Component.text(land.owner?.name ?: player.name)))

        player.sendMessage(Component.translatable("command.info.access").arguments(pluginPrefix, accessMessage))
        player.sendMessage(Component.translatable("command.info.members").arguments(pandorasClusterApi.pluginPrefix(), buildMembers(land)))

        player.sendMessage(Component.translatable("command.info.members.banned").arguments(
            pandorasClusterApi.pluginPrefix(), buildDeniedMembers(land)))

        player.sendMessage(Component.translatable("command.info.flags").arguments(pandorasClusterApi.pluginPrefix(), buildFlags(land)))
        player.sendMessage(Component.translatable("command.info.total-chunk-count").arguments(pluginPrefix, Component.text(land.chunks.size)))
    }

    private fun buildDeniedMembers(land: Land): Component {

       val members = land.members
            .filter(this::filterDeniedMembers).map {
                Component.translatable("command.info.members.entry").arguments(
                    MiniMessage.miniMessage().deserialize(it.role.display),
                    Component.text(it.member.name))
            }.toList()

        return if (members.isNotEmpty())
            Component.join(JoinConfiguration.separator(Component.text(", ")), members) else
            Component.translatable("command.info.members.nobody")
    }

    private fun buildMembers(land: Land): Component {
        val members =
            land.members.filterNot(this::filterDeniedMembers).map {
                Component.translatable("command.info.members.entry").arguments(
                    MiniMessage.miniMessage().deserialize(it.role.display),
                    Component.text(it.member.name))
            }.toList()

        return if (members.isNotEmpty())
            Component.join(JoinConfiguration.separator(Component.text(", ")), members) else
            Component.translatable("command.info.members.nobody")
    }

    private fun buildFlags(land: Land): Component {

        val flags = land.flags.map {
            val value = it.value
            val flagName = it.flag.name

            val booleanFlag = it.flag.type.toInt() == 2

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
            MiniMessage.miniMessage().deserialize("<lang:command.info.flags.entry:\"$flagName\":\"$value\":\"$flagName\":\"$suggestionValue\":\"$symbolColor\">")
        }.toList()

        return if (flags.isNotEmpty()) Component.join(JoinConfiguration.noSeparators(), flags) else MiniMessage.miniMessage().deserialize("<lang:command.info.flags.none>")
    }

    private fun filterDeniedMembers(landMember: LandMember): Boolean = landMember.role == LandRole.BANNED
}
