package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.*
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
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

        val playerId = landPlayer.getUniqueId()
        val playerName = landPlayer.name ?: "null"
        if (playerId == null) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("player-data-not-found", *arrayOf( pluginPrefix, playerName)) })
            return
        }

        if(pandorasClusterApi.hasPlayerLand(playerId)) {
            player.sendMessage(miniMessage {  pandorasClusterApi.i18n("target-player-already-has-land", *arrayOf( pluginPrefix, playerName)) })
            return
        }

        if(land.isOwner(playerId)) {
            player.sendMessage(miniMessage {  pandorasClusterApi.i18n("command.set-owner.nothing-changed", *arrayOf( pluginPrefix, playerName)) })
            return
        }

        if(land.getLandMember(playerId) != null) {
            player.sendMessage(miniMessage {  pandorasClusterApi.i18n("command.set-owner.player-is-member", *arrayOf( pluginPrefix, playerName))})
            return
        }

        this.pandorasClusterApi.getDatabaseStorageService().setLandOwner(land, landPlayer)
        player.sendMessage(miniMessage {  pandorasClusterApi.i18n("command.set-owner.success", pluginPrefix, playerName) })
    }
}