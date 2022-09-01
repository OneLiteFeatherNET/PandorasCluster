package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.miniMessage
import org.apache.logging.log4j.util.Strings
import org.bukkit.entity.Player

class LandInfoCommand(private val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land info")
    @CommandDescription("Get information about the land you standing on")
    @CommandPermission("pandorascluster.command.land.info")
    fun execute(player: Player) {

        val land = pandorasClusterApi.getLand(player.chunk)
        if (land == null) {
            player.sendMessage(miniMessage { "This chunk is not claimed" })
            return
        }

        player.sendMessage(miniMessage { "Owner: ${land.owner?.name ?: player.name}" })
        player.sendMessage(miniMessage { "Members: ${Strings.join(land.landMembers.mapNotNull { it.member?.name }, ',')}" })
        player.sendMessage(miniMessage { "Home: ${land.homePosition.posX} ${land.homePosition.posY} ${land.homePosition.posZ}" })
        player.sendMessage(miniMessage { "Access: ${land.hasAccess(player.uniqueId)}" })
    }
}