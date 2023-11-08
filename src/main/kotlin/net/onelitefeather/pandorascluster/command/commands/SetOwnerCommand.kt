package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.*
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import net.onelitefeather.pandorascluster.extensions.miniMessage
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import org.bukkit.entity.Player

class SetOwnerCommand(private val pandorasClusterApi: PandorasClusterApi) : EntityUtils {

    @CommandMethod("land setowner <player>")
    @CommandPermission("pandorascluster.command.land.setowner")
    @CommandDescription("Set the Owner of the Land")
    @Confirmation
    fun execute(player: Player, @Argument("player", parserName = "landPlayer") landPlayer: LandPlayer) {

        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val land = pandorasClusterApi.getLand(player.chunk)
        if (land == null) {
            player.sendMessage(miniMessage { "<lang:chunk-is-not-claimed:'$pluginPrefix'>" })
            return
        }

        if (!land.isOwner(player.uniqueId) && !hasPermission(player, Permission.SET_LAND_OWNER)) {
            player.sendMessage(miniMessage { "<lang:not-authorized:'$pluginPrefix'>" })
            return
        }

        val targetPlayerId = landPlayer.getUniqueId()
        val targetPlayerName = landPlayer.name ?: "null"
        if (targetPlayerId == null) {
            player.sendMessage(miniMessage { "<lang:player-data-not-found:'$pluginPrefix':'$targetPlayerName'>" })
            return
        }

        if (pandorasClusterApi.hasPlayerLand(targetPlayerId)) {
            player.sendMessage(miniMessage { "<lang:target-player-already-has-land:'$pluginPrefix':'$targetPlayerName'>" })
            return
        }

        if (land.isOwner(targetPlayerId)) {
            player.sendMessage(miniMessage { "<lang:command.set-owner.nothing-changed:'$pluginPrefix':'$targetPlayerName'>" })
            return
        }

        if (land.getLandMember(targetPlayerId) != null) {
            player.sendMessage(miniMessage { "<lang:command.set-owner.player-is-member:'$pluginPrefix':'$targetPlayerName'>" })
            return
        }

        this.pandorasClusterApi.getDatabaseStorageService().setLandOwner(land, landPlayer)
        player.sendMessage(miniMessage { "<lang:command.set-owner.success:'$pluginPrefix':'$targetPlayerName'>" })
    }
}
