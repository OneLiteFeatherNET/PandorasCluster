package net.onelitefeather.pandorascluster.api.position;

import net.onelitefeather.pandorascluster.api.util.NumberUtil;

public record HomePosition(Long id,
                           Double posX,
                           Double posY,
                           Double posZ,
                           Float yaw,
                           Float pitch) {

    public int getBlockX() {
        return NumberUtil.locToBlock(this.posX);
    }

    public int getBlockY() {
        return NumberUtil.locToBlock(this.posY);
    }

    public int getBlockZ() {
        return NumberUtil.locToBlock(this.posZ);
    }
}
