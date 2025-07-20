package net.onelitefeather.pandorascluster;

import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.api.PandorasClusterImpl;
import net.onelitefeather.pandorascluster.listener.PlayerConnectionListener;
import net.onelitefeather.pandorascluster.service.PaperCommandService;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class PandorasClusterPlugin extends JavaPlugin {

    private PandorasCluster pandorasCluster;
    private PaperCommandService paperCommandService;

    @Override
    public void onEnable() {

        this.pandorasCluster = new PandorasClusterImpl();
        this.paperCommandService = new PaperCommandService(this);
        this.paperCommandService.registerCommands();
        // Register the service for third-party plugins to use
        this.getServer().getServicesManager().register(PandorasCluster.class, this.pandorasCluster, this, ServicePriority.Highest);

        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this.pandorasCluster), this);
        getLogger().info("PandorasCluster has been enabled!");
    }

    public PandorasCluster getPandorasCluster() {
        return pandorasCluster;
    }

    @Override
    public void onDisable() {
        getLogger().info("PandorasCluster has been disabled!");
    }
}
