package net.onelitefeather.pandorascluster.command.commands;

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import org.bukkit.entity.Player

class LandToggleBorderCommand(val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land toggleborder")
    @CommandDescription("Shows the border of the land you standing on")
    fun execute(player: Player) {

        val stateMessage = if(pandorasClusterApi.getLandService().toggleShowBorder(player)){
            Component.translatable("command.showborder.toggle.visible")
        } else {
            Component.translatable("command.showborder.toggle.hidden")
        }

        player.sendMessage(Component.translatable("command.showborder.toggle").arguments(
            pandorasClusterApi.pluginPrefix(), stateMessage))

    }
}
