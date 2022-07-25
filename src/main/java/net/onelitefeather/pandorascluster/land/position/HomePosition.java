package net.onelitefeather.pandorascluster.land.position;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@Entity
public class HomePosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private double x;

    @Column
    private double y;

    @Column
    private double z;

    @Column
    private float yaw;

    @Column
    private float pitch;

    @OneToOne
    private Land land;

    public HomePosition() {
    }

    public HomePosition(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Override
    public String toString() {
        return "HomePosition{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }

    public static HomePosition dummyLocation() {
        return of(Bukkit.getWorlds().get(0).getSpawnLocation());
    }

    public static Location fromHomePosition(World world, HomePosition homePosition) {
        return new Location(world, homePosition.getX(), homePosition.getY(), homePosition.getZ(), homePosition.getYaw(), homePosition.getPitch());
    }

    public static HomePosition of(Location location) {
        return new HomePosition(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
}
