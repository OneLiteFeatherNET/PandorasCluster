package net.onelitefeather.pandorascluster.command.commands;

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.PandorasClusterPlugin
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.util.PLUGIN_PREFIX
import org.bukkit.entity.Player

class LandToggleBorderCommand(val pandorasClusterApi: PandorasClusterApi, private val plugin: PandorasClusterPlugin) {

    @CommandMethod("land toggleborder")
    @CommandDescription("Shows the border of the land you standing on")
    fun execute(player: Player) {

        val stateMessage = if(plugin.bukkitLandService.toggleShowBorder(player)){
            Component.translatable("command.showborder.toggle.visible")
        } else {
            Component.translatable("command.showborder.toggle.hidden")
        }

        player.sendMessage(Component.translatable("command.showborder.toggle").arguments(PLUGIN_PREFIX, stateMessage))

    }
}
