package net.onelitefeather.pandorascluster.command.parser

import cloud.commandframework.annotations.parsers.Parser
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.land.flag.findByName
import net.onelitefeather.pandorascluster.land.flag.getDefaultFlagNames
import net.onelitefeather.pandorascluster.util.MATERIALS
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.util.*

class LandFlagParser(private val pandorasClusterApi: PandorasClusterApi) {

    @Parser(name = "landFlag", suggestions = "landFlags")
    fun parseLandFlags(@Suppress("UNUSED_PARAMETER") commandContext: CommandContext<Player>, input: Queue<String>): LandFlag {
        return findByName(input.remove().lowercase()) ?: LandFlag.UNKNOWN
    }

    // isInteractable - no alternative possible at the moment
    @Suppress("DEPRECATION")
    @Suggestions("flag_values")
    fun flagSuggestions(commandContext: CommandContext<Player>, input: String): List<String> {
        val landFlag = commandContext.get<LandFlag>("flag")

        when (landFlag.type.toInt()) {
            0 -> {
                if (commandContext.get<LandFlag>("flag") == LandFlag.USE) {
                    return MATERIALS.filter(Material::isInteractable)
                        .map(Material::toString)
                        .filter { StringUtil.startsWithIgnoreCase(it, input.lowercase()) }
                }
            }

            2 -> return listOf("true", "false")
            else -> return emptyList()
        }

        return emptyList()
    }

    @Suggestions("landFlags")
    fun landFlags(
        @Suppress("UNUSED_PARAMETER") commandContext: CommandContext<Player>,
        @Suppress("UNUSED_PARAMETER") input: String
    ): List<String> {
        return getDefaultFlagNames()
    }
}