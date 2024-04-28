package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.land.position.fromHomePosition
import org.bukkit.entity.Player

class LandTeleportCommands(val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land home")
    fun executeHomeCommand(player: Player) {
        val homePosition = pandorasClusterApi.getLandService().getHome(player.uniqueId) ?: return
        player.teleport(fromHomePosition(player.world, homePosition))
        player.sendMessage(Component.translatable("command.home.success").arguments(pandorasClusterApi.pluginPrefix()))
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
            player.sendMessage(Component.translatable("player-data-not-found").
            arguments(pluginPrefix, Component.text(playerName)))
            return
        }

        val land = pandorasClusterApi.getLand(landOwner)
        if (land == null) {
            player.sendMessage(Component.translatable("player-has-no-land").arguments(pluginPrefix))
            return
        }

        if(land.isBanned(player.uniqueId)) {
            player.sendMessage(Component.translatable("command.visit.banned").arguments(pluginPrefix))
            return
        }

        player.teleport(fromHomePosition(player.world, land.homePosition))
        player.sendMessage(Component.translatable("command.visit.success").arguments(pluginPrefix, Component.text(playerName)))
    }
}