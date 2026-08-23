package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;
import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;

import java.util.List;

public interface FlagRegistryService {

    /**
     *
     * @return a list of all known {@link EntityCapFlag}'s
     */
    List<EntityCapFlag> getEntityCapFlags();

    /**
     *
     * @return a list of all known {@link RoleFlag}'s
     */
    List<RoleFlag> getRoleFlags();

    /**
     *
     * @return a list of all known {@link NaturalFlag}'s
     */
    List<NaturalFlag> getNaturalFlags();

    /**
     *
     * @param name the name of the flag
     * @return an {@link EntityCapFlag} by the giving name
     */
    EntityCapFlag entityCapFlagOf(String name);

    /**
     *
     * @param name the name of the flag
     * @return an {@link RoleFlag} by the giving name
     */
    RoleFlag roleFlagOf(String name);

    /**
     *
     * @param name the name of the flag
     * @return an {@link NaturalFlag} by the giving name
     */
    NaturalFlag naturalFlagOf(String name);

    /**
     * Register all default {@link net.onelitefeather.pandorascluster.api.flag.Flag>'s }
     */
    void registerDefaults();
}
