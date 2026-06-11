package net.onelitefeather.pandorascluster.api.land;

import net.onelitefeather.pandorascluster.api.land.flag.WorldNaturalFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public record LandWorld(Long id,
                        UUID worldId,
                        String name,
                        List<WorldNaturalFlag> naturalFlags,
                        List<Land> lands) {

    @Nullable
    public WorldNaturalFlag getNaturalFlag(String name) {
        return this.naturalFlags()
                .stream()
                .filter(worldNaturalFlag -> worldNaturalFlag.name().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }
}
