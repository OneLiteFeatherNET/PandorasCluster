package net.onelitefeather.pandorascluster.api.flag;

import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class FlagContainer implements PandorasModel {

    public static final FlagContainer EMPTY = new FlagContainer(
            null,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList());

    private final Long id;
    private final List<LandNaturalFlag> naturalFlags;
    private final List<LandRoleFlag> roleFlags;
    private final List<LandEntityCapFlag> entityCapFlags;

    public FlagContainer(Long id,
                         List<LandNaturalFlag> naturalFlags,
                         List<LandRoleFlag> roleFlags,
                         List<LandEntityCapFlag> entityCapFlags) {
        this.id = id;
        this.naturalFlags = naturalFlags;
        this.roleFlags = roleFlags;
        this.entityCapFlags = entityCapFlags;
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

    public void removeRoleFlag(LandRoleFlag roleFlag) {
        this.roleFlags.remove(roleFlag);
    }

    public void removeNaturalFlag(LandNaturalFlag naturalFlag) {
        this.naturalFlags.remove(naturalFlag);
    }

    public void removeEntityCapFlag(LandEntityCapFlag entityCapFlag) {
        this.entityCapFlags.remove(entityCapFlag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlagContainer that)) return false;

        return Objects.equals(id, that.id) &&
                Objects.equals(naturalFlags, that.naturalFlags) &&
                Objects.equals(roleFlags, that.roleFlags) &&
                Objects.equals(entityCapFlags, that.entityCapFlags);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(naturalFlags);
        result = 31 * result + Objects.hashCode(roleFlags);
        result = 31 * result + Objects.hashCode(entityCapFlags);
        return result;
    }
}
