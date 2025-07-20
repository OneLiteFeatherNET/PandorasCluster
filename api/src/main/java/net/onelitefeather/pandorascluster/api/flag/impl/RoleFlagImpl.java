package net.onelitefeather.pandorascluster.api.flag.impl;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.flag.types.DefaultStateFlag;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;

public final class RoleFlagImpl implements RoleFlag {

    private final String name;
    private boolean state;
    private LandRole role;
    private boolean allowInWilderness;

    public RoleFlagImpl(String name, boolean state, LandRole role, boolean allowInWilderness) {
        this.name = name;
        this.state = state;
        this.role = role;
        this.allowInWilderness = allowInWilderness;
    }

    @Override
    public LandRole getRole() {
        return this.role;
    }

    @Override
    public DefaultStateFlag<RoleFlag> role(LandRole role) {
        this.role = role;
        return this;
    }

    @Override
    public Boolean getDefaultState() {
        return this.state;
    }

    @Override
    public RoleFlag defaultState(Boolean newState) {
        this.state = newState;
        return this;
    }

    @Override
    public boolean isAllowedInWilderness() {
        return this.allowInWilderness;
    }

    @Override
    public RoleFlag allowInWilderness(boolean allowWilderness) {
        this.allowInWilderness = allowWilderness;
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
