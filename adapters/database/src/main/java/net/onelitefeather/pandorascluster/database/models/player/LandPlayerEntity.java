package net.onelitefeather.pandorascluster.database.models.player;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.dbo.player.LandPlayerDBO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Entity
@Table(name = "land_players")
public final class LandPlayerEntity implements LandPlayerDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(36)")
    private String uuid;

    @Column(columnDefinition = "VARCHAR(16)")
    private String name;

    public LandPlayerEntity() {
        // Empty constructor for Hibernate
    }

    public LandPlayerEntity(Long id, String uuid, String name) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public @Nullable Long id() {
        return id;
    }

    @Override
    public @NotNull String uuid() {
        return uuid;
    }

    @Override
    public @NotNull String name() {
        return name;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LandPlayerEntity that)) return false;

        return Objects.equals(id, that.id) && Objects.equals(uuid, that.uuid) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(uuid);
        result = 31 * result + Objects.hashCode(name);
        return result;
    }
}

