package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.extensions.miniMessage
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import org.bukkit.entity.Player

class LandRemovePlayerCommand(val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land remove <player>")
    @CommandPermission("pandorascluster.command.land.remove")
    fun execute(player: Player, @Argument(value = "player", parserName = "landPlayer") landPlayer: LandPlayer) {

        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val land = pandorasClusterApi.getLand(player.chunk)
        if (land == null) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("chunk-is-not-claimed", *arrayOf(pluginPrefix)) })
            return
        }

        if(!land.isOwner(player.uniqueId) && !land.isAdmin(player.uniqueId) && !player.hasPermission(Permission.REMOVE_PLAYER_OTHER_LAND)) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("not-authorized", *arrayOf(pluginPrefix)) })
            return
        }

        val targetPlayerId = landPlayer.getUniqueId()
        val targetName = landPlayer.name ?: "null"
        if (targetPlayerId == null) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("player-data-not-found", *arrayOf(pluginPrefix, targetName)) })
            return
        }

        val member = land.getLandMember(targetPlayerId)
        if(member == null) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("command.remove.not-found", *arrayOf(pluginPrefix, targetName)) })
            return
        }

        pandorasClusterApi.getDatabaseStorageService().removeLandMember(member)
        player.sendMessage(miniMessage { pandorasClusterApi.i18n("command.remove.success", *arrayOf(pluginPrefix, targetName)) })
    }
}