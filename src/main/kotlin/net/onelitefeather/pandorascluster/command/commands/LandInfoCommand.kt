package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.Land
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

        player.sendMessage(Component.translatable("command.info.home").arguments(
            pluginPrefix,
            Component.text(land.homePosition.getBlockX()),
            Component.text(land.homePosition.getBlockY()),
            Component.text(land.homePosition.getBlockZ())))

        player.sendMessage(Component.translatable("command.info.flags").arguments(pandorasClusterApi.pluginPrefix(), buildFlags(land)))
        player.sendMessage(Component.translatable("command.info.total-chunk-count").arguments(
            pluginPrefix,
            Component.text(pandorasClusterApi.getLandService().getChunksByLand(land))))
    }

    private fun buildMembers(land: Land): Component {

        val members = land.landMembers.filterNot { it.member?.name == null}.map {
            MiniMessage.miniMessage().deserialize("<lang:command.info.members.entry:\"${it.role.display}\":\"${it.member?.name}\">")
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
                    "<lang:command.info.flag.enabled>"
                } else {
                    "<lang:command.info.flag.disabled>"
                }
            } else {
                "<lang:command.info.flag.disabled>"
            }

            val suggestionValue = if (booleanFlag) !value.toBoolean() else value
            MiniMessage.miniMessage().deserialize("<lang:command.info.flags.entry:\"$flagName\":\"$value\":\"$flagName\":\"$suggestionValue\":\"$symbolColor\">")
        }.toList()

        return if (flags.isNotEmpty()) Component.join(JoinConfiguration.noSeparators(), flags) else
            Component.translatable("command.info.flags.none")
    }
}