package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LandEntityCapFlag implements PandorasModel {

    private final Long id;
    private String name;
    private int spawnLimit;
    private FlagContainer parent;

    public LandEntityCapFlag(Long id, String name, int spawnLimit, FlagContainer parent) {
        this.id = id;
        this.name = name;
        this.spawnLimit = spawnLimit;
        this.parent = parent;
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

    public LandEntityCapFlag withFlag(@NotNull String flag) {
        this.name = flag;
        return this;
    }

    public LandEntityCapFlag withState(int spawnLimit) {
        this.spawnLimit = spawnLimit;
        return this;
    }

    public int getSpawnLimit() {
        return spawnLimit;
    }
}
