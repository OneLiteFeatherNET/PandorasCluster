package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.toMM
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.land.position.HomePosition
import org.bukkit.entity.Player

class LandTeleportCommands(val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land home")
    fun executeHomeCommand(player: Player) {
        val homePosition = pandorasClusterApi.getLandService().getHome(player.uniqueId) ?: return
        player.teleport(HomePosition.fromHomePosition(player.world, homePosition))
    }

    @CommandMethod("land visit <player>")
    @CommandPermission("pandorascluster.command.land.visit")
    fun executeVisitCommand(
        player: Player,
        @Argument(value = "player", parserName = "landPlayer") landOwner: LandPlayer
    ) {

        if (landOwner.uuid == null) {
            player.sendMessage("Nichts gefunden".toMM())
            return
        }

        val land = pandorasClusterApi.getLandService().getLand(landOwner)
        if (land == null) {
            player.sendMessage("Nichts gefunden".toMM())
            return
        }

        if(land.isBanned(player.uniqueId)) {
            player.sendMessage("Du bist auf diesem Land gebannt!".toMM())
            return
        }

        player.teleport(HomePosition.fromHomePosition(player.world, land.homePosition))
        player.sendMessage("Du bist nun auf dem Land von ${land.owner?.name}".toMM())
    }
}