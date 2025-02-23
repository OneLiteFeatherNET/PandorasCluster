package net.onelitefeather.pandorascluster.api.flag.types;

import net.onelitefeather.pandorascluster.api.flag.Flag;

public interface EntityCapFlag extends Flag<EntityCapFlag> {

    Integer getSpawnLimit();
    EntityCapFlag spawnLimit(Integer spawnLimit);
}
