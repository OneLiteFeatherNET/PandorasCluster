package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.toMM
import net.onelitefeather.pandorascluster.land.position.HomePosition
import org.bukkit.entity.Player

class SetHomeCommand(private val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land set home")
    @CommandPermission("pandorascluster.command.land.set.home")
    @CommandDescription("Set the home position of your land to your current position")
    fun execute(player: Player) {

        val land = pandorasClusterApi.getLandService().getFullLand(player.chunk)
        if (land == null) {
            player.sendMessage("Nichts gefunden!".toMM())
            return
        }

        player.sendMessage("Home position was successfully set to your current position".toMM())
        pandorasClusterApi.getDatabaseStorageService().updateLandHome(HomePosition.of(player.location), player.uniqueId)
    }
}