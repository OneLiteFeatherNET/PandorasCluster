package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;
import org.jetbrains.annotations.Nullable;

public record LandEntityCapFlag(Long id, String name, int spawnLimit, FlagContainer parent) {

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSpawnLimit() {
        return spawnLimit;
    }

    public FlagContainer getParent() {
        return parent;
    }

    @Nullable
    public EntityCapFlag getFlag() {
        return FlagRegistry.entityCapFlagOf(this.name);
    }
}
