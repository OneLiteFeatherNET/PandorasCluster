package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.Confirmation
import cloud.commandframework.annotations.specifier.Quoted
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.miniMessage
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.land.flag.isValidValue
import org.bukkit.entity.Player

class SetFlagCommand(private val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land flag set <flag> <value>")
    @CommandPermission("pandorascluster.command.land.flag.set")
    @Confirmation
    fun execute(
        player: Player,
        @Argument("flag", parserName = "landFlag") landFlag: LandFlag,
        @Argument(value = "value", suggestions = "flag_values") @Quoted value: String
    ) {
        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val land = pandorasClusterApi.getLand(player.chunk)
        if (land == null) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("chunk-is-not-claimed", *arrayOf(pluginPrefix)) })
            return
        }

        if(!isValidValue(landFlag, value)) {
            player.sendMessage(miniMessage { "The value $value is not valid for flag $landFlag" })
            return
        }

        if(landFlag == LandFlag.UNKNOWN) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("command.set-flag.not-found", *arrayOf(pluginPrefix)) })
            return
        }

        if(landFlag != LandFlag.USE) {
            pandorasClusterApi.getDatabaseStorageService().updateLandFlag(landFlag, value, land)
        } else {
            pandorasClusterApi.getDatabaseStorageService().addUseMaterial(land, value)

        }
        player.sendMessage(miniMessage { pandorasClusterApi.i18n("command.set-flag.success", *arrayOf(pluginPrefix, landFlag.name, value)) })
    }
}