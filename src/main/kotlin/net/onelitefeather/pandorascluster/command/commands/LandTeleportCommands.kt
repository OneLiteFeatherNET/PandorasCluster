package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.miniMessage
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.land.position.fromHomePosition
import org.bukkit.entity.Player

class LandTeleportCommands(val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land home")
    fun executeHomeCommand(player: Player) {
        val homePosition = pandorasClusterApi.getLandService().getHome(player.uniqueId) ?: return
        player.teleport(fromHomePosition(player.world, homePosition))
        player.sendMessage(miniMessage { "<lang:command.home.success:'${pandorasClusterApi.pluginPrefix()}'>" })
    }

    @CommandMethod("land visit <player>")
    @CommandPermission("pandorascluster.command.land.visit")
    fun executeVisitCommand(
        player: Player,
        @Argument(value = "player", parserName = "landPlayer") landOwner: LandPlayer
    ) {

        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val playerName = landOwner.name ?: "null"
        if (landOwner.uuid == null) {
            player.sendMessage(miniMessage { "<lang:player-data-not-found:'$pluginPrefix':'$playerName'>" })
            return
        }

        val land = pandorasClusterApi.getLand(landOwner)
        if (land == null) {
            player.sendMessage(miniMessage { "<lang:player-has-no-land:'$pluginPrefix'>" })
            return
        }

        if(land.isBanned(player.uniqueId)) {
            player.sendMessage(miniMessage { "<lang:command.visit.banned:'$pluginPrefix'>" })
            return
        }

        player.teleport(fromHomePosition(player.world, land.homePosition))
        player.sendMessage(miniMessage { "<lang:command.visit.success:'$pluginPrefix':'${land.owner?.name ?: "null"}'>" })
    }
}
