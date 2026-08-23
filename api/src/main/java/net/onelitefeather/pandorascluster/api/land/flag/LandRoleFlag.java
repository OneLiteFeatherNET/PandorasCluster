package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.flag.FlagContainer;

public record LandRoleFlag(Long id, String name, Boolean state, LandRole role, FlagContainer parent) implements LandFlag { }
