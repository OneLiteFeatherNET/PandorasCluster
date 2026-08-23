package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;
import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public interface FlagRegistryService {

    List<EntityCapFlag> getEntityCapFlags();

    List<RoleFlag> getRoleFlags();

    List<NaturalFlag> getNaturalFlags();


    @Nullable
    default EntityCapFlag entityCapFlagOf(String name) {
        return getEntityCapFlags().stream()
                .filter(Objects::nonNull)
                .filter(flag -> flag.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    @Nullable
    default RoleFlag roleFlagOf(String name) {
        return getRoleFlags().stream()
                .filter(Objects::nonNull).filter(flag -> flag.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    @Nullable
    default NaturalFlag naturalFlagOf(String name) {
        return getNaturalFlags().stream()
                .filter(Objects::nonNull).filter(flag -> flag.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }
}
