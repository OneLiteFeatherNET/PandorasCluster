package net.onelitefeather.pandorascluster.command.parser

import cloud.commandframework.annotations.parsers.Parser
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.player.LandPlayer
import org.bukkit.command.CommandSender
import java.util.*

class LandPlayerParser(private val pandorasClusterApi: PandorasClusterApi) {

    @Parser(name = "landPlayer", suggestions = "landPlayers")
    fun parseLandPlayer(commandSender: CommandContext<CommandSender>, input: Queue<String>): LandPlayer {
        val name = input.remove()
        return pandorasClusterApi.getLandPlayerService().getLandPlayer(name) ?: return LandPlayer(null, UUID.randomUUID(), name)
    }

    @Suggestions("landPlayers")
    fun landPlayers(commandContext: CommandContext<CommandSender>, input: String): List<String> {
        return pandorasClusterApi.getLandPlayerService().getLandPlayers().map { it.name }
    }
}