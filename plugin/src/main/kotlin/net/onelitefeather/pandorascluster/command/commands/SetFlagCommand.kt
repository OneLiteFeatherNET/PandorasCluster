package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.Confirmation
import cloud.commandframework.annotations.specifier.Quoted
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.enums.Permission
import net.onelitefeather.pandorascluster.api.land.flag.FlagRoleAttachment
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import org.bukkit.entity.Player

class SetFlagCommand(private val pandorasClusterApi: PandorasClusterApi) : EntityUtils, ChunkUtils {

    @CommandMethod("land flag set <flag> <value>")
    @CommandPermission("pandorascluster.command.land.flag.set")
    @Confirmation
    fun execute(
        player: Player,
        @Argument("flag", parserName = "landFlag") landFlag: LandFlag,
        @Argument(value = "value", suggestions = "flag_values") @Quoted value: String
    ) {
        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(player.chunk))
        if (land == null) {
            player.sendMessage(Component.translatable("chunk-is-not-claimed").arguments(pluginPrefix))
            return
        }

        if (!land.isOwner(player.uniqueId) && !land.isAdmin(player.uniqueId) && !hasPermission(player, Permission.SET_LAND_FLAG)) {
            player.sendMessage(Component.translatable("not-authorized").arguments(pluginPrefix))
            return
        }

        if(!FlagRoleAttachment.isValidValue(landFlag, value)) {
            player.sendMessage(Component.translatable("command.set-flag.invalid-value").
            arguments(pluginPrefix, Component.text(value), Component.text(landFlag.name)))
            return
        }

        if(landFlag == LandFlag.UNKNOWN) {
            player.sendMessage(Component.translatable("command.set-flag.not-found").arguments(pluginPrefix))
            return
        }

        pandorasClusterApi.getLandFlagService().updateLandFlag(land.getFlag(landFlag).copy(value = value))
        player.sendMessage(Component.translatable("command.set-flag.success").arguments(
            pluginPrefix,
            Component.text(landFlag.name),
            Component.text(value)))
    }
}