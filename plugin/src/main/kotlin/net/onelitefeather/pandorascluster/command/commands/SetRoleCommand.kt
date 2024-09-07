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
import net.onelitefeather.pandorascluster.enums.LAND_ROLES
import net.onelitefeather.pandorascluster.enums.LandRole
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.enums.getLandRole
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class SetRoleCommand(private val pandorasClusterApi: PandorasClusterApi) : EntityUtils {

    @CommandMethod("land role <player> <role>")
    @CommandDescription("Set a Role for a Player")
    @Confirmation
    fun setRoleCommand(
        player: Player,
        @Argument("player", parserName = "landPlayer") landPlayer: LandPlayer,
        @Greedy @Argument("role", parserName = "landRole") landRole: LandRole
    ) {
        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val land = pandorasClusterApi.getLand(player.chunk)
        val targetPlayerId = landPlayer.getUniqueId()

        if (land == null) {
            player.sendMessage(Component.translatable("chunk-is-not-claimed").arguments(pluginPrefix))
            return
        }

        if(!land.isOwner(player.uniqueId) && !land.isAdmin(player.uniqueId) && !hasPermission(player, Permission.SET_LAND_ROLE)) {
            player.sendMessage(Component.translatable("not-authorized").arguments(pluginPrefix))
            return
        }

        val targetName = landPlayer.name ?: "not found"
        if (targetPlayerId == null) {
            player.sendMessage(
                Component.translatable("player-data-not-found").
            arguments(pluginPrefix, Component.text(targetName)))
            return
        }

        if (!landRole.isGrantAble()) {
            player.sendMessage(Component.translatable("command.set-role.role-not.grantable").arguments(
                pluginPrefix,
                MiniMessage.miniMessage().deserialize(landRole.display)))
            return
        }

        if (land.isOwner(targetPlayerId)) {
            player.sendMessage(Component.translatable("command.set-role.cannot-change-the-land-owner").arguments(pluginPrefix))
            return
        }

        if (land.isOwner(player.uniqueId) || land.isAdmin(player.uniqueId) || hasPermission(player, Permission.SET_LAND_ROLE)) {
            if(landRole != LandRole.VISITOR) {
                pandorasClusterApi.getDatabaseStorageService().addLandMember(land, landPlayer, landRole)
                player.sendMessage(Component.translatable("command.set-role.access").arguments(
                    pluginPrefix,
                    Component.text(targetName),
                    MiniMessage.miniMessage().deserialize(landRole.display)))
            } else {

                val member = land.getLandMember(targetPlayerId)
                if(member == null) {
                    player.sendMessage(Component.translatable("command.remove.not-found").arguments(
                        pluginPrefix,
                        Component.text(targetName)))
                    return
                }

                pandorasClusterApi.getDatabaseStorageService().removeLandMember(member)
                player.sendMessage(Component.translatable("command.remove.success").arguments(
                    pluginPrefix,
                    Component.text(targetName)))
            }
        }
    }

    @Parser(name = "landRole", suggestions = "landRoles")
    fun parseLandRole(commandSender: CommandContext<CommandSender>, input: Queue<String>): LandRole {
        return getLandRole(input.remove()) ?: return LandRole.VISITOR
    }

    @Suggestions("landRoles")
    fun landRoles(commandSender: CommandContext<CommandSender>, input: String): List<String> {
        return LAND_ROLES.filter { it.isGrantAble() }.map { it.name };
    }
}
