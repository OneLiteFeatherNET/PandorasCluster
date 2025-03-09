package net.onelitefeather.pandorascluster.command.mapper.types;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

@SuppressWarnings({"NonExtendableApiUsage", "UnstableApiUsage"})
public class EntitySource implements CommandSourceStack {

    private Entity entity;

    public EntitySource(Entity entity) {
        this.entity = entity;
    }

    @Override
    public @NotNull Location getLocation() {
        return this.entity.getLocation();
    }

    @Override
    public @NotNull CommandSender getSender() {
        return this.entity;
    }

    @Override
    public @Nullable Entity getExecutor() {
        return this.entity;
    }

    @Override
    public @NotNull CommandSourceStack withLocation(Location location) {
        Location currentLocation = this.entity.getLocation();
        currentLocation.set(location.getX(), location.getY(), location.getZ());
        currentLocation.setWorld(location.getWorld());
        currentLocation.setYaw(location.getYaw());
        currentLocation.setPitch(location.getPitch());
        return this;
    }

    @Override
    public @NotNull CommandSourceStack withExecutor(@NotNull Entity entity) {
        this.entity = entity;
        return this;
    }
}
