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
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.extensions.miniMessage
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class SetRoleCommand(private val pandorasClusterApi: PandorasClusterApi) {

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
            player.sendMessage(miniMessage {  pandorasClusterApi.i18n("chunk-is-not-claimed", *arrayOf(pluginPrefix)) })
            return
        }

        if(!land.isOwner(player.uniqueId) && !land.isAdmin(player.uniqueId) && !player.hasPermission(Permission.SET_LAND_ROLE)) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("not-authorized", *arrayOf(pluginPrefix)) })
            return
        }

        val targetName = landPlayer.name ?: "not found"
        if (targetPlayerId == null) {
            player.sendMessage(miniMessage {  pandorasClusterApi.i18n("player-data-not-found", *arrayOf(pluginPrefix, targetName)) })
            return
        }

        if (!landRole.isGrantAble()) {
            player.sendMessage(miniMessage {  pandorasClusterApi.i18n("command.set-role.role-not.grantable", *arrayOf(pluginPrefix, landRole.display)) })
            return
        }

        if (land.isOwner(targetPlayerId)) {
            player.sendMessage(miniMessage {  pandorasClusterApi.i18n("command.set-role.cannot-change-the-land-owner", *arrayOf(pluginPrefix)) })
            return
        }

        if (land.isOwner(player.uniqueId) || land.isAdmin(player.uniqueId) || player.hasPermission(Permission.SET_LAND_ROLE)) {
            if(landRole != LandRole.VISITOR) {
                pandorasClusterApi.getDatabaseStorageService().addLandMember(land, landPlayer, landRole)
                player.sendMessage(miniMessage { pandorasClusterApi.i18n("command.set-role.access", *arrayOf(pluginPrefix, playerName, landRole.display)) })
            } else {

                val member = land.getLandMember(playerId)
                if(member == null) {
                    player.sendMessage(miniMessage { pandorasClusterApi.i18n("command.remove.not-found", *arrayOf(pluginPrefix, playerName)) })
                    return
                }

                pandorasClusterApi.getDatabaseStorageService().removeLandMember(member)
                player.sendMessage(miniMessage { pandorasClusterApi.i18n("command.remove.success", *arrayOf(pluginPrefix, playerName)) })
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