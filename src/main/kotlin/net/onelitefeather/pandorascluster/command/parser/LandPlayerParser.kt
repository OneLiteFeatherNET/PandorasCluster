package net.onelitefeather.pandorascluster.command.parser

import cloud.commandframework.annotations.parsers.Parser
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import org.bukkit.command.CommandSender
import java.util.*

class LandPlayerParser(private val pandorasClusterApi: PandorasClusterApi) {

    @Parser(name = "landPlayer", suggestions = "landPlayers")
    fun parseLandPlayer(commandSender: CommandContext<CommandSender>, input: Queue<String>): LandPlayer {
        val name = input.remove()
        return pandorasClusterApi.getLandPlayer(name) ?: return LandPlayer(null, null, name)
    }

    @Suggestions("landPlayers")
    fun landPlayers(commandContext: CommandContext<CommandSender>, input: String): List<String> {
        return pandorasClusterApi.getLandPlayerService().getPlayers().mapNotNull { it.name }
    }
}