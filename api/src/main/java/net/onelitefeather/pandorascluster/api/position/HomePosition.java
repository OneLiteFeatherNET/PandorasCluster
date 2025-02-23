package net.onelitefeather.pandorascluster.api.position;

import net.onelitefeather.pandorascluster.api.util.NumberUtil;

import java.util.Objects;

public final class HomePosition {

    private Long id;
    private String world;
    private Double posX;
    private Double posY;
    private Double posZ;
    private Float yaw;
    private Float pitch;

    public HomePosition(Long id, String world, Double posX, Double posY, Double posZ, Float yaw, Float pitch) {
        this.id = id;
        this.world = world;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getWorld() {
        return world;
    }

    public int getBlockX() {
        return NumberUtil.locToBlock(this.posX);
    }

    public Double getPosX() {
        return posX;
    }

    public void setPosX(Double posX) {
        this.posX = posX;
    }

    public int getBlockY() {
        return NumberUtil.locToBlock(this.posZ);
    }

    public Double getPosY() {
        return posY;
    }

    public void setPosY(Double posY) {
        this.posY = posY;
    }

    public int getBlockZ() {
        return NumberUtil.locToBlock(this.posY);
    }

    public Double getPosZ() {
        return posZ;
    }

    public void setPosZ(Double posZ) {
        this.posZ = posZ;
    }

    public Float getYaw() {
        return yaw;
    }

    public void setYaw(Float yaw) {
        this.yaw = yaw;
    }

    public Float getPitch() {
        return pitch;
    }

    public void setPitch(Float pitch) {
        this.pitch = pitch;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HomePosition that)) return false;
        return Objects.equals(id, that.id)
                && Objects.equals(posX, that.posX)
                && Objects.equals(posY, that.posY)
                && Objects.equals(posZ, that.posZ)
                && Objects.equals(yaw, that.yaw)
                && Objects.equals(pitch, that.pitch)
                && Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(posX);
        result = 31 * result + Objects.hashCode(posY);
        result = 31 * result + Objects.hashCode(posZ);
        result = 31 * result + Objects.hashCode(yaw);
        result = 31 * result + Objects.hashCode(pitch);
        result = 31 * result + Objects.hashCode(world);
        return result;
    }

    @Override
    public String toString() {
        return "HomePosition{" +
                "id=" + getId() +
                ", posX=" + getPosX() +
                ", posY=" + getPosY() +
                ", posZ=" + getPosZ() +
                ", yaw=" + getYaw() +
                ", pitch=" + getPitch() +
                ", world=" + getWorld() +
                '}';
    }
}
