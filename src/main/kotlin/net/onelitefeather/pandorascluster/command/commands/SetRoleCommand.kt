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
import net.onelitefeather.pandorascluster.enums.LandRole
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

        val land = pandorasClusterApi.getLandService().getFullLand(player.chunk)
        val playerId = landPlayer.getUniqueId()

        if (land == null || playerId == null) {
            player.sendMessage(Component.text("Nichts gefunden!"))
            return
        }

        if (!landRole.isGrantAble()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("Die Rolle ${landRole.name} ist nicht vergebbar"))
            return
        }

        if (land.isOwner(playerId)) {
            player.sendMessage(
                MiniMessage.miniMessage().deserialize("Die Rolle des Land Besitzers kann nicht geändert werden.")
            )
            return
        }

        if (land.isOwner(player.uniqueId) || player.hasPermission("pandorascluster.settings.others")) {
            pandorasClusterApi.getDatabaseStorageService().addLandMember(land, landPlayer, landRole)
            player.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize("Der Spieler ${landPlayer.name} hat nun die Rolle ${landRole.name} auf deinem Land")
            )
        }
    }

    @Parser(name = "landRole", suggestions = "landRoles")
    fun parseLandRole(commandSender: CommandContext<CommandSender>, input: Queue<String>): LandRole {
        return LandRole.getLandRole(input.remove()) ?: return LandRole.MEMBER
    }

    @Suggestions("landRoles")
    fun landRoles(commandSender: CommandContext<CommandSender>, input: String): List<String> {
        return LandRole.landRoles.filter { it.isGrantAble() }.map { it.name };
    }
}