package net.onelitefeather.pandorascluster;

import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.api.PandorasClusterImpl;
import net.onelitefeather.pandorascluster.listener.PlayerConnectionListener;
import org.bukkit.plugin.java.JavaPlugin;

public class PandorasClusterPlugin extends JavaPlugin {

    private PandorasCluster pandorasCluster;

    @Override
    public void onEnable() {
        this.pandorasCluster = new PandorasClusterImpl();
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this.pandorasCluster), this);
        getLogger().info("PandorasCluster has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PandorasCluster has been disabled!");
    }
}
