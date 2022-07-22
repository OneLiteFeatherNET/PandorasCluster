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

        this.addLandFlag("pvp", "false", LandFlagType.PLAYER);
        this.addLandFlag("pve", "false", LandFlagType.PLAYER);

        this.addLandFlag("redstone", "false", LandFlagType.POWERED);
        this.addLandFlag("potion-splash", "false", LandFlagType.ENTITY);

        this.addLandFlag("vehicle-use", "false", LandFlagType.ENTITY);
        this.addLandFlag("vehicle-place", "false", LandFlagType.ENTITY);
        this.addLandFlag("vehicle-break", "false", LandFlagType.ENTITY);
        this.addLandFlag("vehicle-create", "false", LandFlagType.ENTITY);

        this.addLandFlag("interact-containers", "false", LandFlagType.PLAYER);
        this.addLandFlag("leaves-decay", "true", LandFlagType.WORLD_TICK);
        this.addLandFlag("explosions", "false", LandFlagType.EXPLOSION);
        this.addLandFlag("farmland-destroy", "false", LandFlagType.ENTITY);
        this.addLandFlag("mob-griefing", "false", LandFlagType.ENTITY);

        this.addLandFlag("ice-form", "false", LandFlagType.WORLD_TICK);
        this.addLandFlag("block-form", "false", LandFlagType.WORLD_TICK);
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
