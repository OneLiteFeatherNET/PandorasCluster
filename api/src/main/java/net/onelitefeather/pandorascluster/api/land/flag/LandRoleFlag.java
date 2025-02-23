package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LandRoleFlag {

    private final Long id;
    private String name;
    private Boolean state;
    private LandRole role;
    private final Land land;

    public LandRoleFlag(Long id, String name, Boolean state, LandRole role, Land land) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.role = role;
        this.land = land;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public RoleFlag getFlag() {
        return FlagRegistry.roleFlagOf(name);
    }

    public LandRoleFlag withRole(LandRole role) {
        this.role = role;
        return this;
    }

    public LandRole getRole() {
        return role;
    }

    public LandRoleFlag withFlag(@NotNull String flag) {
        this.name = flag;
        return this;
    }

    public LandRoleFlag withState(Boolean state) {
        this.state = state;
        return this;
    }

    public Boolean getState() {
        return state;
    }

    public Land getLand() {
        return land;
    }
}
