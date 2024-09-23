package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.player.LandPlayer
import net.onelitefeather.pandorascluster.extensions.LocationUtils
import org.bukkit.entity.Player

class LandTeleportCommands(val pandorasClusterApi: PandorasClusterApi) : LocationUtils {

    @CommandMethod("land home")
    fun executeHomeCommand(player: Player) {

        val landPlayer = pandorasClusterApi.getLandPlayerService().getLandPlayer(player.uniqueId) ?: return
        val land = pandorasClusterApi.getLandService().getLand(landPlayer) ?: return
        player.teleport(fromHomePosition(player.world, land.home!!))
        player.sendMessage(Component.translatable("command.home.success").arguments(pandorasClusterApi.pluginPrefix()))
    }

    @CommandMethod("land visit <player>")
    @CommandPermission("pandorascluster.command.land.visit")
    fun executeVisitCommand(
        player: Player,
        @Argument(value = "player", parserName = "landPlayer") landOwner: LandPlayer
    ) {

        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val playerName = landOwner.name

        val land = pandorasClusterApi.getLandService().getLand(landOwner)
        if (land == null) {
            player.sendMessage(Component.translatable("player-has-no-land").arguments(pluginPrefix))
            return
        }

        if(land.isBanned(player.uniqueId)) {
            player.sendMessage(Component.translatable("command.visit.banned").arguments(pluginPrefix))
            return
        }

        player.teleport(fromHomePosition(player.world, land.home!!))
        player.sendMessage(Component.translatable("command.visit.success").arguments(pluginPrefix, Component.text(playerName)))
    }
}
