package net.onelitefeather.pandorascluster.api.player;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;

import java.util.UUID;

public record LandPlayer(Long id, UUID uniqueId, String name) implements PandorasModel {

    public Long getId() {
        return id;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }
}
