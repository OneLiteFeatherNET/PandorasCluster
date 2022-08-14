package net.onelitefeather.pandorascluster.command.parser

import cloud.commandframework.annotations.parsers.Parser
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import org.bukkit.entity.Player
import java.util.*
import net.onelitefeather.pandorascluster.land.flag.findByName
import net.onelitefeather.pandorascluster.util.DUMMY_FLAG_ENTITY

class LandFlagParser(private val pandorasClusterApi: PandorasClusterApi) {

    @Parser(name = "landFlag", suggestions = "landFlags")
    fun parseLandFlags(commandContext: CommandContext<Player>, input: Queue<String>): LandFlagEntity {
        val landPlayer =
            pandorasClusterApi.getLandPlayer(commandContext.sender.uniqueId) ?: return DUMMY_FLAG_ENTITY
        val land = pandorasClusterApi.getLandService().getLand(landPlayer) ?: return DUMMY_FLAG_ENTITY

        val flag = findByName(input.remove()) ?: return DUMMY_FLAG_ENTITY

        return pandorasClusterApi.getLandService().getLandFlag(flag, land) ?: return DUMMY_FLAG_ENTITY
    }

    @Suggestions("landFlags")
    fun landPlayers(commandContext: CommandContext<Player>, input: String): List<String> {
        val landPlayer = pandorasClusterApi.getLandPlayer(commandContext.sender.uniqueId) ?: return listOf()
        val land = pandorasClusterApi.getLandService().getLand(landPlayer) ?: return listOf()
        return pandorasClusterApi.getLandService().getFlagsByLand(land).mapNotNull { it.name }
    }
}
