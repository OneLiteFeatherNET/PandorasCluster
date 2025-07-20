package net.onelitefeather.pandorascluster.service;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.format.NamedTextColor;
import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.command.LandCommand;
import net.onelitefeather.pandorascluster.command.mapper.PaperSenderMapper;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.PaperCommandManager;
import org.jetbrains.annotations.NotNull;

public class PaperCommandService {

    private final PandorasClusterPlugin plugin;
    private final PaperCommandManager<CommandSender> paperCommandManager;
    private final AnnotationParser<CommandSender> annotationParser;
    private final MinecraftHelp<CommandSender> minecraftHelp;
    private final BukkitAudiences bukkitAudiences;

    public PaperCommandService(@NotNull PandorasClusterPlugin plugin) {
        this.plugin = plugin;
        this.bukkitAudiences = BukkitAudiences.create(plugin);
        this.paperCommandManager = buildCommandSystem();
        this.annotationParser = buildAnnotationParser();
        this.minecraftHelp = buildHelpSystem();
    }

    @NotNull
    public PaperCommandManager<CommandSender> getPaperCommandManager() {
        return paperCommandManager;
    }

    @NotNull
    public AnnotationParser<CommandSender> getAnnotationParser() {
        return annotationParser;
    }

    @NotNull
    public MinecraftHelp<CommandSender> getMinecraftHelp() {
        return minecraftHelp;
    }

    public void registerCommands() {
        this.annotationParser.parse(new LandCommand(this.plugin.getPandorasCluster()));
    }

    @NotNull
    private MinecraftHelp<CommandSender> buildHelpSystem() {
        return MinecraftHelp.<CommandSender>builder()
                .commandManager(this.paperCommandManager)
                .audienceProvider(this.bukkitAudiences::sender)
                .commandPrefix("/land help")
                .colors(MinecraftHelp.helpColors(
                        NamedTextColor.YELLOW,
                        NamedTextColor.GOLD,
                        NamedTextColor.YELLOW,
                        NamedTextColor.GRAY,
                        NamedTextColor.GOLD))
                .build();
    }

    @NotNull
    private AnnotationParser<CommandSender> buildAnnotationParser() {
        return new AnnotationParser<>(this.paperCommandManager, CommandSender.class);
    }

    @NotNull
    private PaperCommandManager<CommandSender> buildCommandSystem() {
        return PaperCommandManager.builder(new PaperSenderMapper())
                .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
                .buildOnEnable(this.plugin);
    }
}
