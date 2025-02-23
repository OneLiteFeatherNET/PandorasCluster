package net.onelitefeather.pandorascluster.api.flag.impl;

import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;

public class EntityCapImpl implements EntityCapFlag {

    private int spawnLimit;
    private final String name;

    public EntityCapImpl(int spawnLimit, String name) {
        this.spawnLimit = spawnLimit;
        this.name = name;
    }

    @Override
    public Integer getSpawnLimit() {
        return this.spawnLimit;
    }

    @Override
    public EntityCapFlag spawnLimit(Integer spawnLimit) {
        this.spawnLimit = spawnLimit;
        return this;
    }

    @Override
    public boolean isAllowedInWilderness() {
        return true;
    }

    @Override
    public EntityCapFlag allowInWilderness(boolean allowWilderness) {
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
