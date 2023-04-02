package net.onelitefeather.pandorascluster.command.parser

import cloud.commandframework.annotations.parsers.Parser
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.land.flag.findByName
import net.onelitefeather.pandorascluster.land.flag.getDefaultFlagNames
import net.onelitefeather.pandorascluster.util.MATERIALS
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.util.*

class LandFlagParser(private val pandorasClusterApi: PandorasClusterApi) {

    @Parser(name = "landFlag", suggestions = "landFlags")
    fun parseLandFlags(commandContext: CommandContext<Player>, input: Queue<String>): LandFlag {
        return findByName(input.remove().lowercase()) ?: LandFlag.UNKNOWN
    }

    @Suggestions("flag_values")
    fun flagSuggestions(commandContext: CommandContext<Player>, input: String): List<String> {
        val landFlag = commandContext.get<LandFlag>("flag")

        when(landFlag.type.toInt()) {
            0 -> {
                if (commandContext.get<LandFlag>("flag") == LandFlag.USE) {
                    return MATERIALS.filter { StringUtil.startsWithIgnoreCase(it.name, input.lowercase()) }.filter { it.isInteractable }.map { it.name }
                }
            }

            2 -> {
                return listOf("true", "false")
            }

            else -> {
                return emptyList()
            }
        }

        return emptyList()
    }


    @Suggestions("landFlags")
    fun landFlags(commandContext: CommandContext<Player>, input: String): List<String> {
        return getDefaultFlagNames()
    }
}
