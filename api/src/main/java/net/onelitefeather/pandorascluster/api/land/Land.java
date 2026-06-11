package net.onelitefeather.pandorascluster.api.land;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.position.HomePosition;

import java.util.List;
import java.util.UUID;

public record Land(Long id,
                   LandPlayer owner,
                   HomePosition home,
                   List<LandArea> areas,
                   FlagContainer flagContainer,
                   LandWorld world) {

    public Land {
        areas = List.copyOf(areas);
    }

    public LandArea getDefaultArea() {
        return areas.getFirst();
    }

    public boolean isOwner(UUID uuid) {
        return owner.uniqueId().equals(uuid);
    }
}
