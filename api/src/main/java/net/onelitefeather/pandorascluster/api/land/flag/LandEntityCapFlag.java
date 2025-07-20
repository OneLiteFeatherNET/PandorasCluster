package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class LandEntityCapFlag implements PandorasModel {

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

    public FlagContainer getParent() {
        return parent;
    }

    @Nullable
    public EntityCapFlag getFlag() {
        return FlagRegistry.entityCapFlagOf(this.name);
    }

    public LandEntityCapFlag withParent(FlagContainer parent) {
        this.parent = parent;
        return this;
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LandEntityCapFlag that)) return false;

        return spawnLimit == that.spawnLimit &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(parent, that.parent);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + spawnLimit;
        result = 31 * result + Objects.hashCode(parent);
        return result;
    }
}
