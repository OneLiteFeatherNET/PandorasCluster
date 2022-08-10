package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.specifier.Quoted
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.toMM
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.util.DUMMY_FLAG_ENTITY
import org.bukkit.entity.Player

class SetFlagCommand(private val pandorasClusterApi: PandorasClusterApi) {


    @CommandMethod("land flag set <flag> <value>")
    @CommandPermission("pandorascluster.command.land.flag.set")
    fun execute(
        player: Player,
        @Argument("flag", parserName = "landFlag") landFlagEntity: LandFlagEntity,
        @Argument(value = "value") @Quoted value: String
    ) {

        val land = pandorasClusterApi.landService.getFullLand(player.chunk)
        if (land == null) {
            player.sendMessage("Nichts gefunden!".toMM())
            return
        }

        if(landFlagEntity == DUMMY_FLAG_ENTITY) {
            player.sendMessage("The flag not exists".toMM())
            return
        }

        pandorasClusterApi.landService.updateLandFlag(landFlagEntity.copy(value = value))
        player.sendMessage("New value $value")
    }
}