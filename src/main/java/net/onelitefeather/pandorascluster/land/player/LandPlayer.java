package net.onelitefeather.pandorascluster.land.player;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Entity
public final class LandPlayer {

    @Id
    private String uuid;

    @Column
    private String name;

    public LandPlayer() {
    }

    public LandPlayer(@NotNull UUID uuid, @NotNull String name) {
        this.uuid = uuid.toString();
        this.name = name;
    }

    @NotNull
    public UUID getUniqueId() {
        return UUID.fromString(this.uuid);
    }

    public void setUniqueId(@NotNull UUID uuid) {
        this.uuid = uuid.toString();
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Nullable
    public Location getLocation() {
        Player player = Bukkit.getPlayer(this.uuid);
        return player != null ? player.getLocation() : null;
    }

    @NotNull
    public Component getDisplayName() {
        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) return LegacyComponentSerializer.legacyAmpersand().deserialize(this.name);
        return player.displayName();
    }

    @Override
    public String toString() {
        return "LandPlayer{" +
                "uuid=" + uuid +
                ", name='" + getName() + '\'' +
                '}';
    }
}

