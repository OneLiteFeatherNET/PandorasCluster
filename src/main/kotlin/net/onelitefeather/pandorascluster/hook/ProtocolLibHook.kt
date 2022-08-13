package net.onelitefeather.pandorascluster.hook

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.BlockPosition
import net.onelitefeather.pandorascluster.PandorasClusterPlugin
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.InvocationTargetException
import java.util.logging.Level

class ProtocolLibHook {

    fun openSignEditor(player: Player) {

        val pluginName = "ProtocolLib"
        val pluginManager = player.server.pluginManager
        val plugin = JavaPlugin.getPlugin(PandorasClusterPlugin::class.java)

        if (!plugin.description.depend.contains(pluginName)) return
        if (pluginManager.isPluginEnabled(pluginName)) {
            try {
                val location = player.location
                val packetContainer =
                    ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR)

                packetContainer.blockPositionModifier.write(
                    0,
                    BlockPosition(location.blockX, location.blockY, location.blockZ))

                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer)

            } catch (e: InvocationTargetException) {
                plugin.logger.log(
                    Level.SEVERE,
                    String.format("Cannot sent sign editor packet to player %s", player.name),
                    e
                )
            }
        }
    }
}