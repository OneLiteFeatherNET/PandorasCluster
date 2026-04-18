package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.Nullable;

public record LandNaturalFlag(Long id, String name, Boolean state, FlagContainer parent) implements PandorasModel {

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getState() {
        return state;
    }

    public FlagContainer getParent() {
        return parent;
    }

    @Nullable
    public NaturalFlag getFlag() {
        return FlagRegistry.naturalFlagOf(name);
    }
}
