package net.onelitefeather.pandorascluster.command.commands;

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod;
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.miniMessage
import org.bukkit.entity.Player

class LandToggleBorderCommand(val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land toggleborder")
    @CommandDescription("Shows the border of the land you standing on")
    fun execute(player: Player) {

        val stateMessage = if(pandorasClusterApi.getLandService().toggleShowBorder(player)){
            "<lang:command.showborder.toggle.visible>"
        } else {
            "<lang:command.showborder.toggle.hidden>"
        }

        player.sendMessage(miniMessage { "<lang:command.showborder.toggle:'${pandorasClusterApi.pluginPrefix()}':'$stateMessage'>" })
    }
}
