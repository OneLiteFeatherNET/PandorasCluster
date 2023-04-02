package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.miniMessage
import net.onelitefeather.pandorascluster.land.Land
import org.bukkit.Location
import org.bukkit.entity.Player

class LandInfoCommand(private val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land info")
    @CommandDescription("Get information about the land you standing on")
    @CommandPermission("pandorascluster.command.land.info")
    fun execute(player: Player) {

        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val land = pandorasClusterApi.getLand(player.chunk)
        if (land == null) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("chunk-is-not-claimed", *arrayOf(pluginPrefix)) })
            return
        }

        player.sendMessage(miniMessage {
            pandorasClusterApi.i18n(
                "command.info.owner",
                pluginPrefix,
                land.owner?.name ?: player.name
            )
        })

        displayLandHome(player, land)

        player.sendMessage(miniMessage {
            pandorasClusterApi.i18n(
                "command.info.access",
                pluginPrefix,
                if (land.hasAccess(player.uniqueId)) pandorasClusterApi.i18n("boolean-true")
                else pandorasClusterApi.i18n("boolean-false")
            )
        })

        displayMembers(player, land)
        displayFlags(player, land)

        player.sendMessage(miniMessage {
            pandorasClusterApi.i18n(
                "command.info.total-chunk-count",
                *arrayOf(pluginPrefix, pandorasClusterApi.getLandService().getChunksByLand(land))
            )
        })
    }

    private fun displayLandHome(player: Player, land: Land) {
        player.sendMessage(miniMessage {
            pandorasClusterApi.i18n(
                "command.info.home",
                pandorasClusterApi.pluginPrefix(),
                Location.locToBlock(land.homePosition.posX),
                Location.locToBlock(land.homePosition.posY),
                Location.locToBlock(land.homePosition.posZ)
            )
        })
    }

    private fun displayMembers(player: Player, land: Land) {
        player.sendMessage(miniMessage {
            pandorasClusterApi.i18n(
                "command.info.members",
                pandorasClusterApi.pluginPrefix(),
                displayMembers(land)
            )
        })
    }

    private fun displayFlags(player: Player, land: Land) {
        player.sendMessage(miniMessage {
            pandorasClusterApi.i18n(
                "command.info.flags",
                pandorasClusterApi.pluginPrefix(),
                displayFlags(land)
            )
        })
    }

    private fun displayMembers(land: Land): String {
        val out = StringBuilder()
        val separator = ", "
        for (landMember in land.landMembers) {
            if (landMember.member?.name == null) continue
            out.append(
                pandorasClusterApi.i18n(
                    "command.info.members.entry",
                    landMember.role.display,
                    landMember.member.name
                )
            ).append(separator)
        }

        return if (out.isNotEmpty()) out.removeSuffix(separator)
            .toString() else pandorasClusterApi.i18n("command.info.members.nobody")
    }

    private fun displayFlags(land: Land): String {
        val symbol = pandorasClusterApi.i18n("command.info.flag.symbol")
        val stringBuilder = StringBuilder()

        for (landFlag in land.flags) {

            val value = landFlag.value ?: "Unknown Value"
            val flagName = landFlag.name ?: "Unknown Flag"

            val booleanFlag = landFlag.type?.toInt() == 2

            val symbolColor = if (booleanFlag) {
                val booleanValue = value.toBoolean()
                if (booleanValue) {
                    pandorasClusterApi.i18n("command.info.flag.enabled", symbol)
                } else {
                    pandorasClusterApi.i18n("command.info.flag.disabled", symbol)
                }
            } else {
                symbol
            }

            stringBuilder.append(
                pandorasClusterApi.i18n(
                    "command.info.flags.entry",
                    flagName,
                    value,
                    flagName,
                    if (booleanFlag) !value.toBoolean() else value,
                    symbolColor
                )
            )
        }

        return if (stringBuilder.isNotEmpty()) stringBuilder.toString() else pandorasClusterApi.i18n("command.info.flags.none")
    }
}