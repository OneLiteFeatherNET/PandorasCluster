package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.miniMessage
import net.onelitefeather.pandorascluster.land.position.toHomePosition
import org.bukkit.entity.Player

class SetHomeCommand(private val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land set home")
    @CommandPermission("pandorascluster.command.land.set.home")
    @CommandDescription("Set the home position of your land to your current position")
    fun execute(player: Player) {

        val land = pandorasClusterApi.getLand(player.chunk)
        if (land == null) {
            player.sendMessage(miniMessage { "Nichts gefunden!" })
            return
        }

        pandorasClusterApi.getDatabaseStorageService().updateLandHome(toHomePosition(player.location), player.uniqueId)
        player.sendMessage(miniMessage { "Home position was successfully set to your current position" })
    }
}