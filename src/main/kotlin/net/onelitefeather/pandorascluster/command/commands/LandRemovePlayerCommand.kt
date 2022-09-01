package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.miniMessage
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import org.bukkit.entity.Player

class LandRemovePlayerCommand(val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land remove <player>")
    @CommandPermission("pandorascluster.command.land.remove")
    fun execute(player: Player, @Argument(value = "player", parserName = "landPlayer") landPlayer: LandPlayer) {

        val land = pandorasClusterApi.getLand(player.chunk)
        if (land == null) {
            player.sendMessage(miniMessage { "Nichts gefunden" })
            return
        }

        val playerId = landPlayer.getUniqueId();
        if (playerId == null) {
            player.sendMessage(miniMessage { "Der Spieler ${landPlayer.name} existiert nicht" })
            return
        }

        val member = land.getLandMember(playerId) ?: return
        pandorasClusterApi.getDatabaseStorageService().removeLandMember(member)
        player.sendMessage(miniMessage { "Der Spieler ${landPlayer.name} wurde erfolgreich von deinem Land entfernt" })
    }
}