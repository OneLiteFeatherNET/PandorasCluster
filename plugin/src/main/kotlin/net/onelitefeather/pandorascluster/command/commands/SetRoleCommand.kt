package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.Confirmation
import cloud.commandframework.annotations.parsers.Parser
import cloud.commandframework.annotations.specifier.Greedy
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.api.enums.Permission
import net.onelitefeather.pandorascluster.api.player.LandPlayer
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class SetRoleCommand(private val pandorasClusterApi: PandorasClusterApi) : EntityUtils, ChunkUtils {

    @CommandMethod("land role <player> <role>")
    @CommandDescription("Set a Role for a Player")
    @Confirmation
    fun setRoleCommand(
        player: Player,
        @Argument("player", parserName = "landPlayer") landPlayer: LandPlayer,
        @Greedy @Argument("role", parserName = "landRole") landRole: LandRole
    ) {
        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(player.chunk))

        if (land == null) {
            player.sendMessage(Component.translatable("chunk-is-not-claimed").arguments(pluginPrefix))
            return
        }

        if(!land.isOwner(player.uniqueId) && !land.isAdmin(player.uniqueId) && !hasPermission(player, Permission.SET_LAND_ROLE)) {
            player.sendMessage(Component.translatable("not-authorized").arguments(pluginPrefix))
            return
        }

        if (!pandorasClusterApi.getLandPlayerService().playerExists(landPlayer.uniqueId)) {
            player.sendMessage(
                Component.translatable("player-data-not-found").
            arguments(pluginPrefix, Component.text(landPlayer.name)))
            return
        }

        if (!landRole.isGrantAble()) {
            player.sendMessage(Component.translatable("command.set-role.role-not.grantable").arguments(
                pluginPrefix,
                MiniMessage.miniMessage().deserialize(landRole.display)))
            return
        }

        if (land.isOwner(landPlayer.uniqueId)) {
            player.sendMessage(Component.translatable("command.set-role.cannot-change-the-land-owner").arguments(pluginPrefix))
            return
        }

        if (land.isOwner(player.uniqueId) || land.isAdmin(player.uniqueId) || hasPermission(player, Permission.SET_LAND_ROLE)) {
            if(landRole != LandRole.VISITOR) {
                pandorasClusterApi.getLandPlayerService().addLandMember(land, landPlayer, landRole)
                player.sendMessage(Component.translatable("command.set-role.access").arguments(
                    pluginPrefix,
                    Component.text(landPlayer.name),
                    MiniMessage.miniMessage().deserialize(landRole.display)))
            } else {

                val member = land.getLandMember(landPlayer.uniqueId)
                if(member == null) {
                    player.sendMessage(Component.translatable("command.remove.not-found").arguments(
                        pluginPrefix,
                        Component.text(landPlayer.name)))
                    return
                }

                pandorasClusterApi.getLandPlayerService().removeLandMember(member)
                player.sendMessage(Component.translatable("command.remove.success").arguments(
                    pluginPrefix,
                    Component.text(landPlayer.name)))
            }
        }
    }

    @Parser(name = "landRole", suggestions = "landRoles")
    fun parseLandRole(commandSender: CommandContext<CommandSender>, input: Queue<String>): LandRole {
        return LandRole.getLandRole(input.remove()) ?: return LandRole.VISITOR
    }

    @Suggestions("landRoles")
    fun landRoles(commandSender: CommandContext<CommandSender>, input: String): List<String> {
        return LandRole.LAND_ROLES.filter { it.isGrantAble() }.map { it.name }
    }
}