package net.onelitefeather.pandorascluster.command.commands;

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod;
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.miniMessage
import org.bukkit.entity.Player

class LandShowBorderCommand(val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land showborder")
    @CommandDescription("Shows the border of the land you standing on")
    fun execute(player: Player) {

        val stateMessage = if(pandorasClusterApi.getLandService().toggleShowBorder(player)){
            pandorasClusterApi.i18n("command.showborder.toggle.visible")
        } else {
            pandorasClusterApi.i18n("command.showborder.toggle.hidden")
        }

        player.sendMessage(miniMessage { pandorasClusterApi.i18n("command.showborder.toggle",
            *arrayOf(pandorasClusterApi.pluginPrefix(), stateMessage)) })
    }
}
