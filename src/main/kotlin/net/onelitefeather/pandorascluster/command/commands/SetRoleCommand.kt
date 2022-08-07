package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
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
    fun setRoleCommand(
        player: Player,
        @Argument("player", parserName = "landPlayer") landPlayer: LandPlayer,
        @Greedy @Argument("role", parserName = "landRole") landRole: LandRole
    ) {

        val landOwner = pandorasClusterApi.getLandPlayer(player.uniqueId) ?: return
        val land = pandorasClusterApi.landService.getLand(landOwner)

        if (land == null) {
            player.sendMessage(Component.text("Nichts gefunden!"))
            return
        }

        if (!landRole.isGrantAble) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("Die Role ${landRole.name} ist nicht vergebbar"))
            return
        }

        if (land.isOwner(landPlayer.uniqueId)) {
            player.sendMessage(
                MiniMessage.miniMessage().deserialize("Die Rolle des Land Besitzers kann nicht geändert werden.")
            )
            return
        }

        if (land.isOwner(player.uniqueId) || player.hasPermission("pandorascluster.settings.others")) {
            pandorasClusterApi.landService.addLandMember(land, landPlayer, landRole)
            player.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize("Der Spieler ${landPlayer.name} hat nun die Role ${landRole.name} auf deinem Land")
            )
        }
    }

    @Parser(name = "landRole", suggestions = "landRoles")
    fun parseLandRole(commandSender: CommandContext<CommandSender>, input: Queue<String>): LandRole {
        return LandRole.getChunkRole(input.remove()) ?: return LandRole.MEMBER
    }

    @Suggestions("landRoles")
    fun landRoles(commandSender: CommandContext<CommandSender>, input: String): List<String> {
        return LandRole.BY_NAME.values.filter { it.isGrantAble }.map { it.name };
    }
}