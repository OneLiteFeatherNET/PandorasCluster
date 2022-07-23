package net.onelitefeather.pandorascluster.service.services;

import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity;
import net.onelitefeather.pandorascluster.land.flag.LandFlagType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LandFlagService {

    private final List<LandFlagEntity> landFlags;

    public LandFlagService() {
        this.landFlags = new ArrayList<>();

        String value = "false";

        this.addLandFlag("pvp", value, LandFlagType.PLAYER);
        this.addLandFlag("pve", value, LandFlagType.PLAYER);

        this.addLandFlag("redstone", value, LandFlagType.POWERED);
        this.addLandFlag("potion-splash", value, LandFlagType.ENTITY);

        this.addLandFlag("vehicle-use", value, LandFlagType.ENTITY);
        this.addLandFlag("vehicle-place", value, LandFlagType.ENTITY);
        this.addLandFlag("vehicle-break", value, LandFlagType.ENTITY);
        this.addLandFlag("vehicle-create", value, LandFlagType.ENTITY);

        this.addLandFlag("interact-containers", value, LandFlagType.PLAYER);
        this.addLandFlag("leaves-decay", "true", LandFlagType.WORLD_TICK);
        this.addLandFlag("explosions", value, LandFlagType.EXPLOSION);
        this.addLandFlag("farmland-destroy", value, LandFlagType.ENTITY);
        this.addLandFlag("mob-griefing", value, LandFlagType.ENTITY);

        this.addLandFlag("ice-form", value, LandFlagType.WORLD_TICK);
        this.addLandFlag("block-form", value, LandFlagType.WORLD_TICK);
    }

    public List<LandFlagEntity> getLandFlags() {
        return landFlags;
    }

    public void addLandFlag(@NotNull String name, @NotNull String value, @NotNull LandFlagType flagType) {

        this.landFlags.add(new LandFlagEntity.Builder().
                name(name).
                withType(flagType).
                value(value).
                build());
    }

    public LandFlagEntity getFlag(@NotNull String name) {

        LandFlagEntity landFlag = null;

        for (int i = 0; i < this.landFlags.size() && landFlag == null; i++) {
            LandFlagEntity flag = this.landFlags.get(i);
            if (flag.getName().equalsIgnoreCase(name)) {
                landFlag = flag;
            }
        }

        return landFlag;
    }
}
