package net.onelitefeather.pandorascluster.api.flag.impl;

import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;

public class NaturalFlagImpl implements NaturalFlag {

    private final String name;
    private boolean state;
    private boolean allowInWilderness;

    public NaturalFlagImpl(String name, boolean state, boolean allowInWilderness) {
        this.name = name;
        this.state = state;
        this.allowInWilderness = allowInWilderness;
    }

    @Override
    public Boolean getDefaultState() {
        return state;
    }

    @Override
    public NaturalFlag defaultState(Boolean newState) {
        this.state = newState;
        return this;
    }

    @Override
    public boolean isAllowedInWilderness() {
        return allowInWilderness;
    }

    @Override
    public NaturalFlag allowInWilderness(boolean allowWilderness) {
        this.allowInWilderness = allowWilderness;
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
