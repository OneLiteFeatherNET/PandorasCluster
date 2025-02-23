package net.onelitefeather.pandorascluster.api.player;

import java.util.Objects;
import java.util.UUID;

public final class LandPlayer {

    private final Long id;
    private final UUID uniqueId;
    private String name;

    public LandPlayer(Long id, UUID uniqueId, String name) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LandPlayer that)) return false;

        return Objects.equals(id, that.id) && Objects.equals(uniqueId, that.uniqueId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(uniqueId);
        result = 31 * result + Objects.hashCode(name);
        return result;
    }

    @Override
    public String toString() {
        return "LandPlayer{" +
                "id=" + getId() +
                ", uniqueId=" + getUniqueId() +
                ", name='" + getName() + '\'' +
                '}';
    }
}
