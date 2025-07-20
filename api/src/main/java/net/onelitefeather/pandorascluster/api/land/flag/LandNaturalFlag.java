package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LandNaturalFlag implements PandorasModel {

    private final Long id;
    private String name;
    private Boolean state;
    private FlagContainer parent;

    public LandNaturalFlag(Long id, String name, Boolean state, FlagContainer parent) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.parent = parent;
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

    public FlagContainer getParent() {
        return parent;
    }

    public LandNaturalFlag withFlag(@NotNull String name) {
        this.name = name;
        return this;
    }

    public LandNaturalFlag withState(Boolean state) {
        this.state = state;
        return this;
    }

    public LandNaturalFlag withParent(FlagContainer parent) {
        this.parent = parent;
        return this;
    }

    public Boolean getState() {
        return state;
    }
}
