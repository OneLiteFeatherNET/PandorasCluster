package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.miniMessage
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

        player.sendMessage(miniMessage {
            pandorasClusterApi.i18n(
                "command.info.home",
                pluginPrefix,
                Location.locToBlock(land.homePosition.posX),
                Location.locToBlock(land.homePosition.posY),
                Location.locToBlock(land.homePosition.posZ)
            )
        })

        player.sendMessage(miniMessage {
            pandorasClusterApi.i18n(
                "command.info.access",
                pluginPrefix,
                if (land.hasAccess(player.uniqueId)) pandorasClusterApi.i18n("boolean-true")
                else pandorasClusterApi.i18n("boolean-false")
            )
        })

        val out = StringBuilder()
        val separator = ", "
        for (landMember in land.landMembers) {
            if (landMember.member?.name == null) continue
            out.append(pandorasClusterApi.i18n("command.info.members.entry",
                landMember.role.display,
                landMember.member.name)).append(separator)
        }

        player.sendMessage(miniMessage { pandorasClusterApi.i18n(
            "command.info.members",
            pluginPrefix,
            if(out.isNotEmpty()) out.removeSuffix(separator).toString() else pandorasClusterApi.i18n("command.info.members.nobody") ) })
    }
}