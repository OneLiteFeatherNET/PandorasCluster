package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.Confirmation
import cloud.commandframework.annotations.specifier.Quoted
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.miniMessage
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.util.Constants
import org.bukkit.entity.Player

class SetFlagCommand(private val pandorasClusterApi: PandorasClusterApi) {


    @CommandMethod("land flag set <flag> <value>")
    @CommandPermission("pandorascluster.command.land.flag.set")
    @Confirmation
    fun execute(
        player: Player,
        @Argument("flag", parserName = "landFlag") landFlagEntity: LandFlagEntity,
        @Argument(value = "value") @Quoted value: String
    ) {

        val land = pandorasClusterApi.getLandService().getFullLand(player.chunk)
        if (land == null) {
            player.sendMessage("Nichts gefunden!".toMM())
            return
        }

        if(landFlagEntity == Constants.DUMMY_FLAG_ENTITY) {
            player.sendMessage(miniMessage { "The flag not exists" })
            return
        }

        pandorasClusterApi.getDatabaseStorageService().updateLandFlag(landFlagEntity.copy(value = value))
        player.sendMessage(miniMessage { "New value $value" })
    }
}