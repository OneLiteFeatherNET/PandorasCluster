package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.miniMessage
import net.onelitefeather.pandorascluster.land.Land
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class LandInfoCommand(private val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land info")
    @CommandDescription("Get information about the land you standing on")
    @CommandPermission("pandorascluster.command.land.info")
    fun execute(player: Player) {

        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val land = pandorasClusterApi.getLand(player.chunk)
        if (land == null) {
            player.sendMessage(miniMessage { "<lang:chunk-is-not-claimed:'$pluginPrefix'>" })
            return
        }

        val accessMessage = if (land.hasAccess(player.uniqueId)) "<lang:boolean-true>" else "<lang:boolean-false>"

        player.sendMessage(miniMessage { "<lang:command.info.owner:'$pluginPrefix':'${land.owner?.name ?: player.name}'>" })
        player.sendMessage(miniMessage { "<lang:command.info.access:'$pluginPrefix':'$accessMessage'>" })

        player.sendMessage(miniMessage {
            "<lang:command.info.members:'${pandorasClusterApi.pluginPrefix()}':'${
                buildMembers(
                    land
                )
            }'>"
        })

        player.sendMessage(miniMessage {
            "<lang:command.info.home:'" + "${pandorasClusterApi.pluginPrefix()}':'" +
                    "${land.homePosition.getBlockX()}':'" +
                    "${land.homePosition.getBlockY()}':'" +
                    "${land.homePosition.getBlockZ()}'>"
        })

        player.sendMessage(Component.translatable("command.info.flags").arguments(
            MiniMessage.miniMessage().deserialize(pandorasClusterApi.pluginPrefix()), buildFlags(land)))

        val chunkCount = pandorasClusterApi.getLandService().getChunksByLand(land)
        player.sendMessage(miniMessage { "<lang:command.info.total-chunk-count:'$pluginPrefix':'$chunkCount'>" })
    }

    private fun buildMembers(land: Land): String {
        val out = StringBuilder()
        val separator = ", "
        for (landMember in land.landMembers) {
            if (landMember.member?.name == null) continue
            out.append("<lang:command.info.members.entry:\"${landMember.role.display}\":\"${landMember.member.name}\">")
            if (out.isNotEmpty()) {
                out.append(separator)
            }
        }

        return if (out.isNotEmpty()) out.removeSuffix(separator).toString() else "<lang:command.info.members.nobody>"
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

        return if (flags.isNotEmpty()) Component.join(JoinConfiguration.noSeparators(), flags) else MiniMessage.miniMessage().deserialize("<lang:command.info.flags.none>")
    }
}