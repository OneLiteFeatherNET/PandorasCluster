package net.onelitefeather.pandorascluster.database.models.position;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.dbo.position.HomePositionDBO;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Entity
@Table(name = "land_homes")
public final class HomePositionEntity implements HomePositionDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double posX;

    @Column
    private Double posY;

    @Column
    private Double posZ;

    @Column
    private Float yaw;

    @Column
    private Float pitch;

    public HomePositionEntity(Long id, Double posX, Double posY, Double posZ, Float yaw, Float pitch) {
        this.id = id;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HomePositionEntity that)) return false;

        return Objects.equals(id, that.id) && Objects.equals(posX, that.posX) && Objects.equals(posY, that.posY) && Objects.equals(posZ, that.posZ) && Objects.equals(yaw, that.yaw) && Objects.equals(pitch, that.pitch);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(posX);
        result = 31 * result + Objects.hashCode(posY);
        result = 31 * result + Objects.hashCode(posZ);
        result = 31 * result + Objects.hashCode(yaw);
        result = 31 * result + Objects.hashCode(pitch);
        return result;
    }

    @Override
    public @Nullable Long id() {
        return id;
    }

    @Override
    public double posX() {
        return posX;
    }

    @Override
    public double posY() {
        return posY;
    }

    @Override
    public double posZ() {
        return posZ;
    }

    @Override
    public float yaw() {
        return yaw;
    }

    @Override
    public float pitch() {
        return pitch;
    }
}
