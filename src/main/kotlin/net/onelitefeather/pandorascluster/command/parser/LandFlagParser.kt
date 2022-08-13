package net.onelitefeather.pandorascluster.command.parser

import cloud.commandframework.annotations.parsers.Parser
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.util.Constants
import org.bukkit.entity.Player
import java.util.*
import net.onelitefeather.pandorascluster.land.flag.findByName

class LandFlagParser(private val pandorasClusterApi: PandorasClusterApi) {

    @Parser(name = "landFlag", suggestions = "landFlags")
    fun parseLandFlags(commandContext: CommandContext<Player>, input: Queue<String>): LandFlagEntity {
        val landPlayer =
            pandorasClusterApi.getLandPlayer(commandContext.sender.uniqueId) ?: return Constants.DUMMY_FLAG_ENTITY
        val land = pandorasClusterApi.getLandService().getLand(landPlayer) ?: return Constants.DUMMY_FLAG_ENTITY

        val flag = findByName(input.remove()) ?: return Constants.DUMMY_FLAG_ENTITY

        return pandorasClusterApi.getLandService().getLandFlag(flag, land) ?: return Constants.DUMMY_FLAG_ENTITY
    }

    @Suggestions("landFlags")
    fun landPlayers(commandContext: CommandContext<Player>, input: String): List<String> {
        val landPlayer = pandorasClusterApi.getLandPlayer(commandContext.sender.uniqueId) ?: return listOf()
        val land = pandorasClusterApi.getLandService().getLand(landPlayer) ?: return listOf()
        return pandorasClusterApi.getLandService().getFlagsByLand(land).mapNotNull { it.name }
    }
}