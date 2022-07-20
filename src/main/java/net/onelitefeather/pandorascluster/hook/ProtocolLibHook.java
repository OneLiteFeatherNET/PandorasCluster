package net.onelitefeather.pandorascluster.hook;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class ProtocolLibHook {

    private static final String PLUGIN_NAME = "ProtocolLib";

    public static void openSignEditor(@NotNull Player player) {

        PluginManager pluginManager = player.getServer().getPluginManager();
        PandorasClusterPlugin plugin = JavaPlugin.getPlugin(PandorasClusterPlugin.class);

        if (!plugin.getDescription().getDepend().contains(PLUGIN_NAME)) return;
        if (pluginManager.isPluginEnabled(PLUGIN_NAME)) {
            try {
                Location location = player.getLocation();

                PacketContainer packetContainer = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
                packetContainer.getBlockPositionModifier().write(0, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);

            } catch (InvocationTargetException e) {
               plugin.getLogger().log(Level.SEVERE, String.format("Cannot sent sign editor packet to player %s", player.getName()), e);
            }
        }
    }

}
