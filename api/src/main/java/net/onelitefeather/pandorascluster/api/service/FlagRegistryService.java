package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;
import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;

import java.util.List;

public interface FlagRegistryService {

    List<EntityCapFlag> getEntityCapFlags();

    List<RoleFlag> getRoleFlags();

    List<NaturalFlag> getNaturalFlags();

    EntityCapFlag entityCapFlagOf(String name);

    RoleFlag roleFlagOf(String name);

    NaturalFlag naturalFlagOf(String name);
}
