package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LandRoleFlag implements PandorasModel {

    private final Long id;
    private String name;
    private Boolean state;
    private LandRole role;
    private FlagContainer parent;

    public LandRoleFlag(Long id, String name, Boolean state, LandRole role, FlagContainer parent) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.role = role;
        this.parent = parent;
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

    public FlagContainer getParent() {
        return parent;
    }

    public LandRole getRole() {
        return role;
    }

    public LandRoleFlag withRole(LandRole role) {
        this.role = role;
        return this;
    }

    public LandRoleFlag withFlag(@NotNull String flag) {
        this.name = flag;
        return this;
    }

    public LandRoleFlag withState(Boolean state) {
        this.state = state;
        return this;
    }

    public LandRoleFlag withParent(FlagContainer parent) {
        this.parent = parent;
        return this;
    }

    public Boolean getState() {
        return state;
    }
}
