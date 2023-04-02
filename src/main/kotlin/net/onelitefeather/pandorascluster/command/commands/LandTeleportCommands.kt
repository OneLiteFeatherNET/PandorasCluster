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
        player.sendMessage(miniMessage { pandorasClusterApi.i18n(
            "command.home.success", *arrayOf(pandorasClusterApi.pluginPrefix())) })
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
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("player-data-not-found", *arrayOf(pluginPrefix, playerName)) })
            return
        }

        val land = pandorasClusterApi.getLand(landOwner)
        if (land == null) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("player-has-no-land", *arrayOf(pluginPrefix)) })
            return
        }

        if(land.isBanned(player.uniqueId)) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("command.visit.banned", *arrayOf(pluginPrefix)) })
            return
        }

        player.teleport(fromHomePosition(player.world, land.homePosition))
        player.sendMessage(miniMessage { pandorasClusterApi.i18n("command.visit.success", *arrayOf(pluginPrefix, land.owner?.name ?: "null"))})
    }
}
