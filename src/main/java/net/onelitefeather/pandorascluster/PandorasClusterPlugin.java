package net.onelitefeather.pandorascluster;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import net.kyori.adventure.text.format.NamedTextColor;
import net.onelitefeather.pandorascluster.service.*;
import net.onelitefeather.pandorascluster.service.LandService1;
import net.onelitefeather.pandorascluster.commands.ClaimCommand;
import net.onelitefeather.pandorascluster.commands.LandCommand;
import net.onelitefeather.pandorascluster.listener.*;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;
import java.util.logging.Level;

public class PandorasClusterPlugin extends JavaPlugin implements Listener {

    private LandPlayerService landPlayerService;
    private LandService1 landService1;
    private DatabaseService databaseService;

    private LandFlagService landFlagService;

    private PaperCommandManager<CommandSender> paperCommandManager;
    private AnnotationParser<CommandSender> annotationParser;
    private MinecraftHelp<CommandSender> minecraftHelp;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        PandorasClusterApiImpl api = new PandorasClusterApiImpl(this);
        this.getServer().getServicesManager().register(PandorasClusterApi.class, api, this, ServicePriority.Highest);

        String jdbcUrl = getConfig().getString("database.jdbcUrl", "'jdbc:mariadb://localhost:3306/playerkits?useSSL=false'");
        String databaseDriver = getConfig().getString("database.driver", "org.mariadb.jdbc.Driver");
        String username = getConfig().getString("database.username", "root");
        String password = getConfig().getString("database.password", "TopSecret");

        this.databaseService = new DatabaseService(this, jdbcUrl, username, password, databaseDriver);
        this.databaseService.init();

        this.landFlagService = new LandFlagService();

        this.landService1 = new LandService1(this);

        this.landPlayerService = new LandPlayerService(this);
        this.landPlayerService.load();

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerConnectionListener(this), this);
        pluginManager.registerEvents(new BlockProtectionListener(this), this);
        pluginManager.registerEvents(new EntityProtectionListener(this, this.landFlagService), this);
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new VehicleProtectionListener(this.landService1, this.landFlagService), this);
        pluginManager.registerEvents(new ContainerProtectionListener(this), this);
        pluginManager.registerEvents(new MenuListener(), this);

        buildCommandSystem();
    }

    @Override
    public void onDisable() {
        this.getServer().getServicesManager().unregisterAll(this);
        this.databaseService.shutdown();
    }

    public PaperCommandManager<CommandSender> getPaperCommandManager() {
        return paperCommandManager;
    }

    public AnnotationParser<CommandSender> getAnnotationParser() {
        return annotationParser;
    }

    public MinecraftHelp<CommandSender> getMinecraftHelp() {
        return minecraftHelp;
    }

    public EntityDataStoreService getEntityDataStoreService() {
        return entityDataStoreService;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public LandPlayerService getChunkPlayerService() {
        return landPlayerService;
    }

    public LandFlagService getChunkFlagService() {
        return landFlagService;
    }

    public LandService1 getWorldChunkManager() {
        return landService1;
    }

    private void buildCommandSystem() {
        try {
            this.paperCommandManager = new PaperCommandManager<>(this, CommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity());
        } catch (Exception e) {
            this.getLogger().log(Level.WARNING, "Failed to build command system", e);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (this.paperCommandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.paperCommandManager.registerBrigadier();
            this.getLogger().info("Brigadier support enabled");
        }
        if (this.paperCommandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.paperCommandManager.registerAsynchronousCompletions();
            this.getLogger().info("Asynchronous completions enabled");
        }

        final Function<ParserParameters, CommandMeta> commandMetaFunction = p -> CommandMeta.simple().with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description")).build();
        this.annotationParser = new AnnotationParser<>(this.paperCommandManager, CommandSender.class, commandMetaFunction);

        this.minecraftHelp = MinecraftHelp.createNative("/pandorascluster help", this.paperCommandManager);
        this.minecraftHelp.setHelpColors(MinecraftHelp.HelpColors.of(NamedTextColor.DARK_GREEN, NamedTextColor.GREEN, NamedTextColor.BLUE, NamedTextColor.DARK_BLUE, NamedTextColor.AQUA));
        annotationParser.parse(new ClaimCommand(this));
        annotationParser.parse(new LandCommand(this));
    }
}
