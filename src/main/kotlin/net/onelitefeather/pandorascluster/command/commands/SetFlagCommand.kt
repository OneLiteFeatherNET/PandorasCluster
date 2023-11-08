package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.Confirmation
import cloud.commandframework.annotations.specifier.Quoted
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.hasPermission
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
            player.sendMessage(miniMessage { "<lang:chunk-is-not-claimed:'$pluginPrefix'>" })
            return
        }

        if (!land.isOwner(player.uniqueId) && !land.isAdmin(player.uniqueId) && !player.hasPermission(Permission.SET_LAND_FLAG)) {
            player.sendMessage(miniMessage { "<lang:not-authorized:'$pluginPrefix'>" })
            return
        }

        if(!isValidValue(landFlag, value)) {
            player.sendMessage(miniMessage { "<lang:command.set-flag.invalid-value:'$pluginPrefix':'$value':'$landFlag'>" })
            return
        }

        if(landFlag == LandFlag.UNKNOWN) {
            player.sendMessage(miniMessage { "<lang:command.set-flag.not-found:'$pluginPrefix'>" })
            return
        }

        if(landFlag != LandFlag.USE) {
            pandorasClusterApi.getDatabaseStorageService().updateLandFlag(landFlag, value, land)
        } else {
            pandorasClusterApi.getDatabaseStorageService().addUseMaterial(land, value)
        }

        player.sendMessage(miniMessage { "<lang:command.set-flag.success:'$pluginPrefix':'${landFlag.name}':'$value'>" })
    }
}
