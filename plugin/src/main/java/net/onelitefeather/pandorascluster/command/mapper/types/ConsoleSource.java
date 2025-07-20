package net.onelitefeather.pandorascluster.command.mapper.types;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

@SuppressWarnings({"NonExtendableApiUsage", "UnstableApiUsage"})
public class ConsoleSource implements CommandSourceStack {

    private final ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();

    @Override
    public @NotNull Location getLocation() {
        return new Location(Bukkit.getWorlds().getFirst(), 0, 0, 0);
    }

    @Override
    public @NotNull CommandSender getSender() {
        return this.consoleCommandSender;
    }

    @Override
    public @Nullable Entity getExecutor() {
        return null;
    }

    @Override
    public @NotNull CommandSourceStack withLocation(@NotNull Location location) {
        throw new IllegalStateException("Cannot set location for console sender");
    }

    @Override
    public @NotNull CommandSourceStack withExecutor(@NotNull Entity entity) {
        throw new IllegalStateException("Cannot set executor for console sender");
    }
}