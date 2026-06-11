package net.onelitefeather.pandorascluster.api.flag;

import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public record FlagContainer(Long id,
                            List<LandNaturalFlag> naturalFlags,
                            List<LandRoleFlag> roleFlags,
                            List<LandEntityCapFlag> entityCapFlags) {

    public static final FlagContainer EMPTY = new FlagContainer(
            null,
            List.of(),
            List.of(),
            List.of());

    public FlagContainer {
        naturalFlags = List.copyOf(naturalFlags);
        roleFlags = List.copyOf(roleFlags);
        entityCapFlags = List.copyOf(entityCapFlags);
    }

    @Nullable
    public LandRoleFlag getRoleFlag(String name) {
        return roleFlags.stream()
                .filter(flag -> flag.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public LandNaturalFlag getNaturalFlag(String name) {
        return naturalFlags.stream()
                .filter(flag -> flag.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public LandEntityCapFlag getEntityCapFlag(String name) {
        return entityCapFlags.stream()
                .filter(flag -> flag.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns all flags in a single list, typed over the sealed {@link LandFlag}
     * interface. Use this with an exhaustive switch when behaviour is uniform
     * across flag variants.
     */
    public List<LandFlag> getAllFlags() {
        return Stream.of(naturalFlags, roleFlags, entityCapFlags)
                .flatMap(List::stream)
                .map(LandFlag.class::cast)
                .toList();
    }

    public boolean hasFlag(@NotNull LandFlag landFlag) {
        return getAllFlags().stream().anyMatch(flag -> flag.name().equalsIgnoreCase(landFlag.name()));
    }
}
