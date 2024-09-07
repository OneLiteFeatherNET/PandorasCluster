package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import net.onelitefeather.pandorascluster.land.position.toHomePosition
import org.bukkit.entity.Player

class SetHomeCommand(private val pandorasClusterApi: PandorasClusterApi) : EntityUtils {

    @CommandMethod("land set home")
    @CommandPermission("pandorascluster.command.land.set.home")
    @CommandDescription("Set the home position of your land to your current position")
    fun execute(player: Player) {

        val pluginPrefix = pandorasClusterApi.pluginPrefix()
        val land = pandorasClusterApi.getLand(player.chunk)
        if (land == null) {
            player.sendMessage(Component.translatable("chunk-is-not-claimed").arguments(pluginPrefix))
            return
        }

        if(!land.isOwner(player.uniqueId) && !land.isAdmin(player.uniqueId) && !hasPermission(player, Permission.SET_LAND_HOME)) {
            player.sendMessage(Component.translatable("not-authorized").arguments(pluginPrefix))
            return
        }

        pandorasClusterApi.getDatabaseStorageService().updateLandHome(toHomePosition(player.location), player.uniqueId)
        player.sendMessage(Component.translatable("command.set-home.success").arguments(pluginPrefix))
    }
}
