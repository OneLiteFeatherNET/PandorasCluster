package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.*
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.enums.Permission
import net.onelitefeather.pandorascluster.api.player.LandPlayer
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import org.bukkit.entity.Player

class SetOwnerCommand(private val pandorasClusterApi: PandorasClusterApi) : EntityUtils, ChunkUtils {

    @CommandMethod("land setowner <player>")
    @CommandPermission("pandorascluster.command.land.setowner")
    @CommandDescription("Set the Owner of the Land")
    @Confirmation
    fun execute(player: Player, @Argument("player", parserName = "landPlayer") landPlayer: LandPlayer) {

        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(player.chunk))
        if (land == null) {
            player.sendMessage(Component.translatable("chunk-is-not-claimed").arguments(pluginPrefix))
            return
        }

        if (!land.isOwner(player.uniqueId) && !hasPermission(player, Permission.SET_LAND_OWNER)) {
            player.sendMessage(Component.translatable("not-authorized").arguments(pluginPrefix))
            return
        }

        if (!pandorasClusterApi.getLandPlayerService().playerExists(landPlayer.uniqueId)) {
            player.sendMessage(
                Component.translatable("player-data-not-found").
                arguments(pluginPrefix, Component.text(landPlayer.name)))
            return
        }

        if (pandorasClusterApi.getLandService().hasPlayerLand(landPlayer)) {
            player.sendMessage(Component.translatable("target-player-already-has-land").arguments(
                pluginPrefix,
                Component.text(landPlayer.name)))
            return
        }

        if (land.isOwner(landPlayer.uniqueId)) {
            player.sendMessage(Component.translatable("command.set-owner.nothing-changed").arguments(
                pluginPrefix,
                Component.text(landPlayer.name)))
            return
        }

        if (land.getLandMember(landPlayer.uniqueId) != null) {
            player.sendMessage(Component.translatable("command.set-owner.player-is-member").arguments(
                pluginPrefix,
                Component.text(landPlayer.name)))
            return
        }

        this.pandorasClusterApi.getLandService().updateLand(land.copy(owner = landPlayer))
        player.sendMessage(Component.translatable("command.set-owner.success").arguments(
            pluginPrefix,
            Component.text(landPlayer.name)))
    }
}
