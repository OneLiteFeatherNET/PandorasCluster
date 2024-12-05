package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.Confirmation
import cloud.commandframework.annotations.specifier.Quoted
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.api.enums.Permission
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import net.onelitefeather.pandorascluster.util.PLUGIN_PREFIX
import org.bukkit.entity.Player

class SetFlagCommand(private val pandorasClusterApi: PandorasClusterApi) : EntityUtils, ChunkUtils {

    @CommandMethod("land flag add <flag> <role>")
    @CommandPermission("pandorascluster.command.land.flag.set")
    @Confirmation
    fun execute(
        player: Player,
        @Argument("flag", parserName = "landFlag") landFlag: LandFlag,
        @Argument(value = "role") @Quoted landRole: LandRole
    ) {

        val land = pandorasClusterApi.getLandService().getLand(player.chunk.chunkKey)
        if (land == null) {
            player.sendMessage(Component.translatable("chunk-is-not-claimed").arguments(PLUGIN_PREFIX))
            return
        }

        if (!land.isAdmin(player.uniqueId) && !hasPermission(player, Permission.SET_LAND_FLAG)) {
            player.sendMessage(Component.translatable("not-authorized").arguments(PLUGIN_PREFIX))
            return
        }

        val flag = land.getFlag(landFlag)
        if (flag == null) {
            pandorasClusterApi.getLandFlagService().addLandFlag(landFlag, landRole, land)
        } else {
            pandorasClusterApi.getLandFlagService().updateLandFlag(flag.copy(role = landRole), land)
        }

        player.sendMessage(
            Component.translatable("command.set-flag.success").arguments(
                PLUGIN_PREFIX,
                Component.text(landFlag.name),
                MiniMessage.miniMessage().deserialize(landRole.display)
            )
        )
    }
}