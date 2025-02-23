package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LandNaturalFlag {

    private final Long id;
    private String name;
    private Boolean state;

    public LandNaturalFlag(Long id, String name, Boolean state) {
        this.id = id;
        this.name = name;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public NaturalFlag getFlag() {
        return FlagRegistry.naturalFlagOf(name);
    }

    public LandNaturalFlag withFlag(@NotNull String name) {
        this.name = name;
        return this;
    }

    public LandNaturalFlag withState(Boolean state) {
        this.state = state;
        return this;
    }

    public Boolean getState() {
        return state;
    }
}
