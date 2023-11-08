package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.Confirmation
import cloud.commandframework.annotations.parsers.Parser
import cloud.commandframework.annotations.specifier.Greedy
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.LAND_ROLES
import net.onelitefeather.pandorascluster.enums.LandRole
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.enums.getLandRole
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import net.onelitefeather.pandorascluster.extensions.miniMessage
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
            player.sendMessage(miniMessage { "<lang:chunk-is-not-claimed:'${pluginPrefix}'>" })
            return
        }

        if(!land.isOwner(player.uniqueId) && !land.isAdmin(player.uniqueId) && !hasPermission(player, Permission.SET_LAND_ROLE)) {
            player.sendMessage(miniMessage { "<lang:not-authorized:'${pluginPrefix}'>" })
            return
        }

        val targetName = landPlayer.name ?: "not found"
        if (targetPlayerId == null) {
            player.sendMessage(miniMessage { "<lang:player-data-not-found:'${pluginPrefix}':'${targetName}'>" })
            return
        }

        if (!landRole.isGrantAble()) {
            player.sendMessage(miniMessage { "<lang:command.set-role.role-not.grantable:'${pluginPrefix}':'${landRole.display}'>" })
            return
        }

        if (land.isOwner(targetPlayerId)) {
            player.sendMessage(miniMessage { "<lang:command.set-role.cannot-change-the-land-owner:'${pluginPrefix}'>" })
            return
        }

        if (land.isOwner(player.uniqueId) || land.isAdmin(player.uniqueId) || hasPermission(player, Permission.SET_LAND_ROLE)) {
            if(landRole != LandRole.VISITOR) {
                pandorasClusterApi.getDatabaseStorageService().addLandMember(land, landPlayer, landRole)
                player.sendMessage(miniMessage { "<lang:command.set-role.access:'${pluginPrefix}':'${targetName}':'${landRole.display}'>" })
            } else {

                val member = land.getLandMember(targetPlayerId)
                if(member == null) {
                    player.sendMessage(miniMessage { "<lang:command.remove.not-found:'${pluginPrefix}':'${targetName}'>" })
                    return
                }

                pandorasClusterApi.getDatabaseStorageService().removeLandMember(member)
                player.sendMessage(miniMessage { "<lang:command.remove.success:'${pluginPrefix}':'${targetName}'>" })
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