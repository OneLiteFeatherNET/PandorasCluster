package net.onelitefeather.pandorascluster.api.land;

import net.onelitefeather.pandorascluster.api.land.flag.AreaEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.AreaNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.AreaRoleFlag;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.position.HomePosition;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Land {

    private Long id;
    private LandPlayer owner;
    private HomePosition home;
    private final List<LandArea> areas;
    private final List<AreaNaturalFlag> naturalFlags;
    private final List<AreaRoleFlag> roleFlags;
    private final List<AreaEntityCapFlag> entityCapFlags;
    private String world;

    public Land(Long id,
                LandPlayer owner,
                HomePosition home,
                List<LandArea> areas,
                List<AreaNaturalFlag> naturalFlags,
                List<AreaRoleFlag> roleFlags,
                List<AreaEntityCapFlag> entityCapFlags,
                String world) {
        this.id = id;
        this.owner = owner;
        this.home = home;
        this.areas = areas;
        this.naturalFlags = naturalFlags;
        this.roleFlags = roleFlags;
        this.entityCapFlags = entityCapFlags;
        this.world = world;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LandPlayer getOwner() {
        return owner;
    }

    public void setOwner(LandPlayer owner) {
        this.owner = owner;
    }

    public HomePosition getHome() {
        return home;
    }

    public void setHome(HomePosition home) {
        this.home = home;
    }

    public List<LandArea> getAreas() {
        return areas;
    }

    public List<AreaNaturalFlag> getNaturalFlags() {
        return naturalFlags;
    }

    public List<AreaRoleFlag> getRoleFlags() {
        return roleFlags;
    }

    public List<AreaEntityCapFlag> getEntityCapFlags() {
        return entityCapFlags;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public LandArea getDefaultArea() {
        return areas.getFirst();
    }

    public boolean isOwner(UUID uuid) {
        return owner.getUniqueId().equals(uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Land land)) return false;

        return Objects.equals(id, land.id) &&
                Objects.equals(owner, land.owner) &&
                Objects.equals(home, land.home) &&
                Objects.equals(areas, land.areas) &&
                Objects.equals(world, land.world);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(owner);
        result = 31 * result + Objects.hashCode(home);
        result = 31 * result + Objects.hashCode(areas);
        result = 31 * result + Objects.hashCode(world);
        return result;
    }

    @Override
    public String toString() {
        return "Land{" +
                "id=" + getId() +
                ", owner=" + getOwner() +
                ", home=" + getHome() +
                ", areas=" + getAreas() +
                ", world='" + getWorld() + '\'' +
                '}';
    }
}
