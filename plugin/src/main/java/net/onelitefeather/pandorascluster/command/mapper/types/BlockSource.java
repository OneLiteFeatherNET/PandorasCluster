package net.onelitefeather.pandorascluster.command.mapper.types;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

@SuppressWarnings({"NonExtendableApiUsage", "UnstableApiUsage"})
public class BlockSource implements CommandSourceStack {

    private final BlockCommandSender blockCommandSender;

    public BlockSource(BlockCommandSender blockCommandSender) {
        this.blockCommandSender = blockCommandSender;
    }

    @Override
    public @NotNull Location getLocation() {
        return this.blockCommandSender.getBlock().getLocation();
    }

    @Override
    public @NotNull CommandSender getSender() {
        return this.blockCommandSender;
    }

    @Override
    public @Nullable Entity getExecutor() {
        return null;
    }

    @Override
    public @NotNull CommandSourceStack withLocation(Location location) {
        Location currentLocation = this.blockCommandSender.getBlock().getLocation();
        currentLocation.set(location.getX(), location.getY(), location.getZ());
        currentLocation.setWorld(location.getWorld());
        currentLocation.setYaw(location.getYaw());
        currentLocation.setPitch(location.getPitch());
        return this;
    }

    @Override
    public @NotNull CommandSourceStack withExecutor(@NotNull Entity entity) {
        return this;
    }
}
