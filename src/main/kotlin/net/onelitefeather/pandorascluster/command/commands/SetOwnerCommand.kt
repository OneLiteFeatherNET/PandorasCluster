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

        val land = pandorasClusterApi.getLandService().getFullLand(player.chunk)
        if(land == null) {
            player.sendMessage(miniMessage { "Du musst auf deinem Land stehen" })
            return
        }

        val playerId = landPlayer.getUniqueId();
        if (playerId == null) {
            player.sendMessage(miniMessage { "Der Spieler ${landPlayer.name} existiert nicht" })
            return
        }

        if(land.isOwner(playerId)) {
            player.sendMessage(miniMessage { "Nothing changed player ${landPlayer.name} is already the land owner" })
            return
        }

        if(land.getLandMember(playerId) != null) {
            player.sendMessage(miniMessage { "The player ${landPlayer.name} is already a member of this Land" })
            return
        }

        this.pandorasClusterApi.getDatabaseStorageService().setLandOwner(land, landPlayer)
        player.sendMessage(miniMessage { "Owner changed" })
    }
}