package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.*
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.enums.Permission
import net.onelitefeather.pandorascluster.api.player.LandPlayer
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import net.onelitefeather.pandorascluster.util.PLUGIN_PREFIX
import org.bukkit.entity.Player

class SetOwnerCommand(private val pandorasClusterApi: PandorasClusterApi) : EntityUtils, ChunkUtils {

    @CommandMethod("land setowner <player>")
    @CommandPermission("pandorascluster.command.land.setowner")
    @CommandDescription("Set the Owner of the Land")
    @Confirmation
    fun execute(player: Player, @Argument("player", parserName = "landPlayer") landPlayer: LandPlayer) {

        val land = pandorasClusterApi.getLandService().getLand(player.chunk.chunkKey)
        if (land == null) {
            player.sendMessage(Component.translatable("chunk-is-not-claimed").arguments(PLUGIN_PREFIX))
            return
        }

        if (!land.isOwner(player.uniqueId) && !hasPermission(player, Permission.SET_LAND_OWNER)) {
            player.sendMessage(Component.translatable("not-authorized").arguments(PLUGIN_PREFIX))
            return
        }

        if (!pandorasClusterApi.getLandPlayerService().playerExists(landPlayer.uniqueId)) {
            player.sendMessage(
                Component.translatable("player-data-not-found").
                arguments(PLUGIN_PREFIX, Component.text(landPlayer.name)))
            return
        }

        if (pandorasClusterApi.getLandService().hasPlayerLand(landPlayer)) {
            player.sendMessage(Component.translatable("target-player-already-has-land").arguments(
                PLUGIN_PREFIX,
                Component.text(landPlayer.name)))
            return
        }

        if (land.isOwner(landPlayer.uniqueId)) {
            player.sendMessage(Component.translatable("command.set-owner.nothing-changed").arguments(
                PLUGIN_PREFIX,
                Component.text(landPlayer.name)))
            return
        }

        if (land.getLandMember(landPlayer.uniqueId) != null) {
            player.sendMessage(Component.translatable("command.set-owner.player-is-member").arguments(
                PLUGIN_PREFIX,
                Component.text(landPlayer.name)))
            return
        }

        this.pandorasClusterApi.getLandService().updateLand(land.copy(owner = landPlayer))
        player.sendMessage(Component.translatable("command.set-owner.success").arguments(
            PLUGIN_PREFIX,
            Component.text(landPlayer.name)))
    }
}