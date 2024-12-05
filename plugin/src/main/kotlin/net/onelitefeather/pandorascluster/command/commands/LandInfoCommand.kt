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
import net.onelitefeather.pandorascluster.util.PLUGIN_PREFIX
import org.bukkit.entity.Player

class LandInfoCommand(private val pandorasClusterApi: PandorasClusterApi) : ChunkUtils {

    @CommandMethod("land info")
    @CommandDescription("Get information about the land you standing on")
    @CommandPermission("pandorascluster.command.land.info")
    fun execute(player: Player) {

        val land = pandorasClusterApi.getLandService().getLand(player.chunk.chunkKey)
        if (land == null) {
            player.sendMessage(Component.translatable("chunk-is-not-claimed").arguments(PLUGIN_PREFIX))
            return
        }

        val accessMessage = if (land.hasMemberAccess(player.uniqueId))
            Component.translatable("boolean-true") else Component.translatable("boolean-false")

        player.sendMessage(Component.translatable("command.info.owner").arguments(PLUGIN_PREFIX,
            Component.text(land.owner?.name ?: player.name)))

        player.sendMessage(Component.translatable("command.info.access").arguments(PLUGIN_PREFIX, accessMessage))
        player.sendMessage(Component.translatable("command.info.members").arguments(PLUGIN_PREFIX, buildMembers(land)))

        player.sendMessage(Component.translatable("command.info.members.banned").arguments(PLUGIN_PREFIX, buildDeniedMembers(land)))

        player.sendMessage(Component.translatable("command.info.flags").arguments(PLUGIN_PREFIX, buildFlags(land)))
        player.sendMessage(Component.translatable("command.info.total-chunk-count").arguments(PLUGIN_PREFIX, Component.text(land.chunks.size)))
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
            val flagName = it.flag.name
            val symbolColor = Component.translatable("command.info.flag.enabled")
            Component.translatable("command.info.flags.entry").arguments(
                Component.text(flagName),
                MiniMessage.miniMessage().deserialize(it.role.display),
                symbolColor)
        }.toList()

        return if (flags.isNotEmpty()) Component.join(JoinConfiguration.noSeparators(), flags) else
            Component.translatable("command.info.flags.none")
    }

    private fun filterDeniedMembers(landMember: LandMember): Boolean = landMember.role == LandRole.BANNED
}
