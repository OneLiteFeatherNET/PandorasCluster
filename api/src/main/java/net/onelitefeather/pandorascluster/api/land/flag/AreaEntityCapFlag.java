package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AreaEntityCapFlag {

    private final Long id;
    private String name;
    private int spawnLimit;

    public AreaEntityCapFlag(Long id, String name, int spawnLimit) {
        this.id = id;
        this.name = name;
        this.spawnLimit = spawnLimit;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public EntityCapFlag getFlag() {
        return FlagRegistry.entityCapFlagOf(this.name);
    }

    public AreaEntityCapFlag withFlag(@NotNull String flag) {
        this.name = flag;
        return this;
    }

    public AreaEntityCapFlag withState(int spawnLimit) {
        this.spawnLimit = spawnLimit;
        return this;
    }

    public int getSpawnLimit() {
        return spawnLimit;
    }
}
