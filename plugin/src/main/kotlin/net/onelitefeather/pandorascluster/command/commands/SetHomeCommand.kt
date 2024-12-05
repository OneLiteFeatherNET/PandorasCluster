package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.enums.Permission
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import net.onelitefeather.pandorascluster.extensions.LocationUtils
import net.onelitefeather.pandorascluster.util.PLUGIN_PREFIX
import org.bukkit.entity.Player

class SetHomeCommand(private val pandorasClusterApi: PandorasClusterApi) : EntityUtils, LocationUtils, ChunkUtils {

    @CommandMethod("land set home")
    @CommandPermission("pandorascluster.command.land.set.home")
    @CommandDescription("Set the home position of your land to your current position")
    fun execute(player: Player) {

        val land = pandorasClusterApi.getLandService().getLand(player.chunk.chunkKey)
        if (land == null) {
            player.sendMessage(Component.translatable("chunk-is-not-claimed").arguments(PLUGIN_PREFIX))
            return
        }

        if(!land.isAdmin(player.uniqueId) && !hasPermission(player, Permission.SET_LAND_HOME)) {
            player.sendMessage(Component.translatable("not-authorized").arguments(PLUGIN_PREFIX))
            return
        }

        pandorasClusterApi.getDatabaseStorageService().updateLandHome(toHomePosition(player.location), player.uniqueId)
        player.sendMessage(Component.translatable("command.set-home.success").arguments(PLUGIN_PREFIX))
    }
}
