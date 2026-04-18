package net.onelitefeather.pandorascluster.database.models.position;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Entity
@Table(name = "land_homes")
public final class HomePositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "world", length = 64, nullable = false)
    private String world;

    @Column(name = "pos_x", nullable = false)
    private Double posX;

    @Column(name = "pos_y", nullable = false)
    private Double posY;

    @Column(name = "pos_z", nullable = false)
    private Double posZ;

    @Column(name = "yaw", nullable = false)
    private Float yaw;

    @Column(name = "pitch", nullable = false)
    private Float pitch;

    public HomePositionEntity() {
        // Empty constructor for Hibernate
    }

    public HomePositionEntity(Long id, String world, Double posX, Double posY, Double posZ, Float yaw, Float pitch) {
        this.id = id;
        this.world = world;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public @Nullable Long id() {
        return id;
    }

    public @NotNull String world() {
        return world;
    }

    public double posX() {
        return posX;
    }

    public double posY() {
        return posY;
    }

    public double posZ() {
        return posZ;
    }

    public float yaw() {
        return yaw;
    }

    public float pitch() {
        return pitch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HomePositionEntity that)) return false;

        return Objects.equals(id, that.id) &&
                Objects.equals(world, that.world) &&
                Objects.equals(posX, that.posX) &&
                Objects.equals(posY, that.posY) &&
                Objects.equals(posZ, that.posZ) &&
                Objects.equals(yaw, that.yaw) &&
                Objects.equals(pitch, that.pitch);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(world);
        result = 31 * result + Objects.hashCode(posX);
        result = 31 * result + Objects.hashCode(posY);
        result = 31 * result + Objects.hashCode(posZ);
        result = 31 * result + Objects.hashCode(yaw);
        result = 31 * result + Objects.hashCode(pitch);
        return result;
    }
}
