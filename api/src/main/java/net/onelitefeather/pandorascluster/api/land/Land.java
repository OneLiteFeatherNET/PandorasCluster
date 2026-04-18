package net.onelitefeather.pandorascluster.api.land;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.position.HomePosition;

import java.util.List;
import java.util.UUID;

public record Land(Long id,
                   LandPlayer owner,
                   HomePosition home,
                   List<LandArea> areas,
                   FlagContainer flagContainer) implements PandorasModel {

    public Land {
        areas = List.copyOf(areas);
    }

    public Long getId() {
        return id;
    }

    public LandPlayer getOwner() {
        return owner;
    }

    public HomePosition getHome() {
        return home;
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
}
