package net.onelitefeather.pandorascluster.command.parser

import cloud.commandframework.annotations.parsers.Parser
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.land.flag.findByName
import net.onelitefeather.pandorascluster.land.flag.getDefaultFlagNames
import org.bukkit.entity.Player
import java.util.*

class LandFlagParser(private val pandorasClusterApi: PandorasClusterApi) {

    @Parser(name = "landFlag", suggestions = "landFlags")
    fun parseLandFlags(commandContext: CommandContext<Player>, input: Queue<String>): LandFlag {
        return findByName(input.remove().lowercase()) ?: LandFlag.UNKNOWN
    }

    @Suggestions("landFlags")
    fun landFlags(commandContext: CommandContext<Player>, input: String): List<String> {
        return getDefaultFlagNames()
    }
}