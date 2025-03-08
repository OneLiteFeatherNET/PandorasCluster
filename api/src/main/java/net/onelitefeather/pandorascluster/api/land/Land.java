package net.onelitefeather.pandorascluster.api.land;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.position.HomePosition;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Land implements PandorasModel {

    private Long id;
    private LandPlayer owner;
    private final FlagContainer flagContainer;
    private HomePosition home;
    private final List<LandArea> areas;

    public Land(Long id,
                LandPlayer owner,
                HomePosition home,
                List<LandArea> areas,
                FlagContainer flagContainer) {
        this.id = id;
        this.owner = owner;
        this.home = home;
        this.areas = areas;
        this.flagContainer = flagContainer;
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

    public FlagContainer getFlagContainer() {
        return flagContainer;
    }

    public String getWorld() {
        return this.home.getWorld();
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
                Objects.equals(areas, land.areas);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(owner);
        result = 31 * result + Objects.hashCode(home);
        result = 31 * result + Objects.hashCode(areas);
        return result;
    }

    @Override
    public String toString() {
        return "Land{" +
                "id=" + getId() +
                ", owner=" + getOwner() +
                ", flagContainer=" + getFlagContainer() +
                ", home=" + getHome() +
                ", areas=" + getAreas() +
                '}';
    }
}
