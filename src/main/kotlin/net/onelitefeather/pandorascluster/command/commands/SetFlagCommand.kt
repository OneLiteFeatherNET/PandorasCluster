package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.Confirmation
import cloud.commandframework.annotations.specifier.Quoted
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.land.flag.isValidValue
import org.bukkit.entity.Player

class SetFlagCommand(private val pandorasClusterApi: PandorasClusterApi) : EntityUtils {

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
            player.sendMessage(Component.translatable("chunk-is-not-claimed").arguments(pluginPrefix))
            return
        }

        if (!land.isOwner(player.uniqueId) && !land.isAdmin(player.uniqueId) && !hasPermission(player, Permission.SET_LAND_FLAG)) {
            player.sendMessage(Component.translatable("not-authorized").arguments(pluginPrefix))
            return
        }

        if(!isValidValue(landFlag, value)) {
            player.sendMessage(Component.translatable("command.set-flag.invalid-value").
            arguments(pluginPrefix, Component.text(value), Component.text(landFlag.name)))
            return
        }

        if(landFlag == LandFlag.UNKNOWN) {
            player.sendMessage(Component.translatable("command.set-flag.not-found").arguments(pluginPrefix))
            return
        }

        if(landFlag != LandFlag.USE) {
            pandorasClusterApi.getDatabaseStorageService().updateLandFlag(landFlag, value, land)
        } else {
            pandorasClusterApi.getDatabaseStorageService().addUseMaterial(land, value)
        }
        player.sendMessage(Component.translatable("command.set-flag.success").arguments(
            pluginPrefix,
            Component.text(landFlag.name),
            Component.text(value)))
    }
}