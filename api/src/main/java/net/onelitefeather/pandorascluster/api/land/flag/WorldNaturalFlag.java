package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;
import net.onelitefeather.pandorascluster.api.land.LandWorld;
import org.jetbrains.annotations.Nullable;

public record WorldNaturalFlag(Long id, String name, Boolean state, LandWorld world) {

    @Nullable
    public NaturalFlag getFlag() {
        return FlagRegistry.naturalFlagOf(name);
    }
}

