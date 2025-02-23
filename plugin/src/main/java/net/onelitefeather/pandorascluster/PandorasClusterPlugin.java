package net.onelitefeather.pandorascluster;

import org.bukkit.plugin.java.JavaPlugin;

public class PandorasClusterPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("PandorasCluster has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PandorasCluster has been disabled!");
    }
}
