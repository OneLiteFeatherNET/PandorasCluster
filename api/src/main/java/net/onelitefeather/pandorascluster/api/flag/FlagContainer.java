package net.onelitefeather.pandorascluster.api.flag;

import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;

import java.util.List;

public record FlagContainer(Long id,
                            List<LandNaturalFlag> naturalFlags,
                            List<LandRoleFlag> roleFlags,
                            List<LandEntityCapFlag> entityCapFlags) implements PandorasModel {

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

    public Long getId() {
        return id;
    }

    public List<LandNaturalFlag> getNaturalFlags() {
        return naturalFlags;
    }

    public List<LandRoleFlag> getRoleFlags() {
        return roleFlags;
    }

    public List<LandEntityCapFlag> getEntityCapFlags() {
        return entityCapFlags;
    }
}
