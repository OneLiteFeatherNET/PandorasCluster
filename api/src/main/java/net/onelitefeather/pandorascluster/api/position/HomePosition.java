package net.onelitefeather.pandorascluster.api.position;

import net.onelitefeather.pandorascluster.api.util.NumberUtil;

public record HomePosition(Long id,
                           String world,
                           Double posX,
                           Double posY,
                           Double posZ,
                           Float yaw,
                           Float pitch) {

    public Long getId() {
        return id;
    }

    public String getWorld() {
        return world;
    }

    public Double getPosX() {
        return posX;
    }

    public Double getPosY() {
        return posY;
    }

    public Double getPosZ() {
        return posZ;
    }

    public Float getYaw() {
        return yaw;
    }

    public Float getPitch() {
        return pitch;
    }

    public int getBlockX() {
        return NumberUtil.locToBlock(this.posX);
    }

    public int getBlockY() {
        return NumberUtil.locToBlock(this.posZ);
    }

    public int getBlockZ() {
        return NumberUtil.locToBlock(this.posY);
    }
}
