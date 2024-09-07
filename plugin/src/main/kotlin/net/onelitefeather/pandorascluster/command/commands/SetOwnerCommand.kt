package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.*
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import org.bukkit.entity.Player

class SetOwnerCommand(private val pandorasClusterApi: PandorasClusterApi) : EntityUtils {

    @CommandMethod("land setowner <player>")
    @CommandPermission("pandorascluster.command.land.setowner")
    @CommandDescription("Set the Owner of the Land")
    @Confirmation
    fun execute(player: Player, @Argument("player", parserName = "landPlayer") landPlayer: LandPlayer) {

        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val land = pandorasClusterApi.getLand(player.chunk)
        if (land == null) {
            player.sendMessage(Component.translatable("chunk-is-not-claimed").arguments(pluginPrefix))
            return
        }

        if (!land.isOwner(player.uniqueId) && !hasPermission(player, Permission.SET_LAND_OWNER)) {
            player.sendMessage(Component.translatable("not-authorized").arguments(pluginPrefix))
            return
        }

        val targetPlayerId = landPlayer.getUniqueId()
        val targetPlayerName = landPlayer.name ?: "null"
        if (targetPlayerId == null) {
            player.sendMessage(
                Component.translatable("player-data-not-found").
                arguments(pluginPrefix, Component.text(targetPlayerName)))
            return
        }

        if (pandorasClusterApi.hasPlayerLand(targetPlayerId)) {
            player.sendMessage(Component.translatable("target-player-already-has-land").arguments(
                pluginPrefix,
                Component.text(targetPlayerName)))
            return
        }

        if (land.isOwner(targetPlayerId)) {
            player.sendMessage(Component.translatable("command.set-owner.nothing-changed").arguments(
                pluginPrefix,
                Component.text(targetPlayerName)))
            return
        }

        if (land.getLandMember(targetPlayerId) != null) {
            player.sendMessage(Component.translatable("command.set-owner.player-is-member").arguments(
                pluginPrefix,
                Component.text(targetPlayerName)))
            return
        }

        this.pandorasClusterApi.getDatabaseStorageService().setLandOwner(land, landPlayer)
        player.sendMessage(Component.translatable("command.set-owner.success").arguments(
            pluginPrefix,
            Component.text(targetPlayerName)))
    }
}
