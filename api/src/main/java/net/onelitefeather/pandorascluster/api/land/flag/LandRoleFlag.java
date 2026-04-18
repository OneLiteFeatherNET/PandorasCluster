package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.Nullable;

public record LandRoleFlag(Long id, String name, Boolean state, LandRole role, FlagContainer parent) implements PandorasModel {

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getState() {
        return state;
    }

    public LandRole getRole() {
        return role;
    }

    public FlagContainer getParent() {
        return parent;
    }

    @Nullable
    public RoleFlag getFlag() {
        return FlagRegistry.roleFlagOf(name);
    }
}
