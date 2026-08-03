package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;
import org.jetbrains.annotations.Nullable;

public record LandNaturalFlag(Long id, String name, Boolean state, FlagContainer parent) implements LandFlag {

    @Nullable
    public NaturalFlag getFlag() {
        return FlagRegistry.naturalFlagOf(name);
    }
}
