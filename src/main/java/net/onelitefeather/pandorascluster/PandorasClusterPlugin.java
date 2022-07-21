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
import net.onelitefeather.pandorascluster.api.PandorasClusterApi;
import net.onelitefeather.pandorascluster.api.PandorasClusterApiImpl;
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

    private PaperCommandManager<CommandSender> paperCommandManager;
    private AnnotationParser<CommandSender> annotationParser;
    private MinecraftHelp<CommandSender> minecraftHelp;

    private PandorasClusterApiImpl api;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        this.api = new PandorasClusterApiImpl(this);
        this.getServer().getServicesManager().register(PandorasClusterApi.class, this.api, this, ServicePriority.Highest);

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerConnectionListener(this.api), this);
        pluginManager.registerEvents(new BlockProtectionListener(this), this);
        pluginManager.registerEvents(new EntityProtectionListener(this, this.api.getLandFlagService()), this);
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new VehicleProtectionListener(this.api.getLandService(), this.api.getLandFlagService()), this);
        pluginManager.registerEvents(new ContainerProtectionListener(this), this);

        buildCommandSystem();
    }

    @Override
    public void onDisable() {
        this.api.getDatabaseService().shutdown();
        this.getServer().getServicesManager().unregisterAll(this);
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
        annotationParser.parse(new ClaimCommand(this.api));
        annotationParser.parse(new LandCommand(this.api));
    }
}
