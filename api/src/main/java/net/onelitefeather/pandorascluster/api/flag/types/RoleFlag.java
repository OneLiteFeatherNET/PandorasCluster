package net.onelitefeather.pandorascluster.api.flag.types;

import net.onelitefeather.pandorascluster.api.enums.LandRole;

public interface RoleFlag extends DefaultStateFlag<RoleFlag> {

    LandRole getRole();
    DefaultStateFlag<RoleFlag> role(LandRole role);
}
