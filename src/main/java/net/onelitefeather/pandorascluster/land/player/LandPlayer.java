package net.onelitefeather.pandorascluster.land.player;

import jakarta.persistence.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

@Entity
public class LandPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String uuid;

    @Column
    private String name;

    public LandPlayer() {
    }

    public LandPlayer(@NotNull UUID uuid, @NotNull String name) {
        this.uuid = uuid.toString();
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LandPlayer that)) return false;
        if (!Objects.equals(uuid, that.uuid)) return false;
        return Objects.equals(name, that.name);
    }
}

