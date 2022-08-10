package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.toMM
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import org.bukkit.entity.Player

class SetOwnerCommand(private val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land setowner <player>")
    @CommandPermission("pandorascluster.command.land.setowner")
    @CommandDescription("Set the Owner of the Land")
    fun execute(player: Player, @Argument("player", parserName = "landPlayer") landPlayer: LandPlayer) {

        val land = pandorasClusterApi.landService.getFullLand(player.chunk)
        if (land == null) {
            player.sendMessage("Nichts gefunden!".toMM())
            return
        }

        if(land.isOwner(landPlayer.uniqueId)) {
            player.sendMessage("Nothing changed player ${landPlayer.name} is already the land owner".toMM())
            return
        }

        this.pandorasClusterApi.landService.setLandOwner(land, landPlayer)
        player.sendMessage("Owner changed".toMM())
    }
}