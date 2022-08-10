package net.onelitefeather.pandorascluster.command.parser

import cloud.commandframework.annotations.parsers.Parser
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.service.LandService
import net.onelitefeather.pandorascluster.util.DUMMY_FLAG_ENTITY
import org.bukkit.entity.Player
import java.util.*

class LandFlagParser(private val pandorasClusterApi: PandorasClusterApi) {

    @Parser(name = "landFlag", suggestions = "landFlags")
    fun parseLandFlags(commandContext: CommandContext<Player>, input: Queue<String>): LandFlagEntity {

        val name = input.remove()
        val landPlayer =
            pandorasClusterApi.getLandPlayer(commandContext.sender.uniqueId) ?: return DUMMY_FLAG_ENTITY
        val land = pandorasClusterApi.landService.getLand(landPlayer) ?: return DUMMY_FLAG_ENTITY

        val flag = LandFlag.findByName(name) ?: return DUMMY_FLAG_ENTITY

        return pandorasClusterApi.landService.getLandFlag(flag, land) ?: return DUMMY_FLAG_ENTITY
    }

    @Suggestions("landFlags")
    fun landPlayers(commandContext: CommandContext<Player>, input: String): List<String> {
        val landPlayer = pandorasClusterApi.getLandPlayer(commandContext.sender.uniqueId) ?: return listOf()
        val land = pandorasClusterApi.landService.getLand(landPlayer) ?: return listOf()
        return pandorasClusterApi.landService.getFlagsByLand(land).map { it.name!! }
    }
}