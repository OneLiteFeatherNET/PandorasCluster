package net.onelitefeather.pandorascluster.api.player;


import java.util.UUID;

public record LandPlayer(Long id, UUID uniqueId, String name) {

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
