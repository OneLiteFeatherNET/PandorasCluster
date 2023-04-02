package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.*
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.extensions.miniMessage
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import org.bukkit.entity.Player

class SetOwnerCommand(private val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land setowner <player>")
    @CommandPermission("pandorascluster.command.land.setowner")
    @CommandDescription("Set the Owner of the Land")
    @Confirmation
    fun execute(player: Player, @Argument("player", parserName = "landPlayer") landPlayer: LandPlayer) {

        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val land = pandorasClusterApi.getLand(player.chunk)
        if (land == null) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("chunk-is-not-claimed", pluginPrefix) })
            return
        }

        if (!land.isOwner(player.uniqueId) && !player.hasPermission(Permission.SET_LAND_OWNER)) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("not-authorized", *arrayOf(pluginPrefix)) })
            return
        }

        val targetPlayerId = landPlayer.getUniqueId()
        val targetPlayerName = landPlayer.name ?: "null"
        if (targetPlayerId == null) {
            player.sendMessage(miniMessage {
                pandorasClusterApi.i18n(
                    "player-data-not-found",
                    *arrayOf(pluginPrefix, targetPlayerName)
                )
            })
            return
        }

        if (pandorasClusterApi.hasPlayerLand(targetPlayerId)) {
            player.sendMessage(miniMessage {
                pandorasClusterApi.i18n(
                    "target-player-already-has-land",
                    *arrayOf(pluginPrefix, targetPlayerName)
                )
            })
            return
        }

        if (land.isOwner(targetPlayerId)) {
            player.sendMessage(miniMessage {
                pandorasClusterApi.i18n(
                    "command.set-owner.nothing-changed",
                    *arrayOf(pluginPrefix, targetPlayerName)
                )
            })
            return
        }

        if (land.getLandMember(targetPlayerId) != null) {
            player.sendMessage(miniMessage {
                pandorasClusterApi.i18n(
                    "command.set-owner.player-is-member",
                    *arrayOf(pluginPrefix, targetPlayerName)
                )
            })
            return
        }

        this.pandorasClusterApi.getDatabaseStorageService().setLandOwner(land, landPlayer)
        player.sendMessage(miniMessage {
            pandorasClusterApi.i18n(
                "command.set-owner.success",
                pluginPrefix,
                targetPlayerName
            )
        })
    }
}
