package net.onelitefeather.pandorascluster.api.flag;

import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;

import java.util.List;
import java.util.Objects;

public class FlagContainer {

    private final Land land;
    private final List<LandNaturalFlag> naturalFlags;
    private final List<LandRoleFlag> roleFlags;
    private final List<LandEntityCapFlag> entityCapFlags;

    public FlagContainer(Land land,
                         List<LandNaturalFlag> naturalFlags,
                         List<LandRoleFlag> roleFlags,
                         List<LandEntityCapFlag> entityCapFlags) {
        this.naturalFlags = naturalFlags;
        this.roleFlags = roleFlags;
        this.entityCapFlags = entityCapFlags;
        this.land = land;
    }

    public Land getLand() {
        return land;
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlagContainer that)) return false;

        return Objects.equals(land, that.land) &&
                Objects.equals(naturalFlags, that.naturalFlags) &&
                Objects.equals(roleFlags, that.roleFlags) &&
                Objects.equals(entityCapFlags, that.entityCapFlags);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(land);
        result = 31 * result + Objects.hashCode(naturalFlags);
        result = 31 * result + Objects.hashCode(roleFlags);
        result = 31 * result + Objects.hashCode(entityCapFlags);
        return result;
    }
}
