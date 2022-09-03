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
        val playerId = landPlayer.getUniqueId()

        if (land == null) {
            player.sendMessage(miniMessage {  pandorasClusterApi.i18n("chunk-is-not-claimed", *arrayOf(pluginPrefix)) })
            return
        }

        val playerName = landPlayer.name ?: "not found"
        if (playerId == null) {
            player.sendMessage(miniMessage {  pandorasClusterApi.i18n("player-data-not-found", *arrayOf(pluginPrefix, playerName)) })
            return
        }

        if (!landRole.isGrantAble()) {
            player.sendMessage(miniMessage {  pandorasClusterApi.i18n("command.set-role.role-not.grantable", *arrayOf(pluginPrefix, landRole.display)) })
            return
        }

        if (land.isOwner(playerId)) {
            player.sendMessage(miniMessage {  pandorasClusterApi.i18n("command.set-role.cannot-change-the-land-owner", *arrayOf(pluginPrefix)) })
            return
        }

        if (land.isOwner(player.uniqueId) || player.hasPermission(Permission.SET_LAND_ROLE)) {
            pandorasClusterApi.getDatabaseStorageService().addLandMember(land, landPlayer, landRole)
            player.sendMessage(
                miniMessage { pandorasClusterApi.i18n("command.set-role.access", *arrayOf(pluginPrefix, playerName, landRole.display)) }
            )
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