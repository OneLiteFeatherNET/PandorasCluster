package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.flag.Flag;
import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;
import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import org.jetbrains.annotations.NotNull;

public interface LandFlagService {

    /**
     * @param roleFlag the flag
     * @param land the land to add the flag
     */
    void addRoleFlag(@NotNull RoleFlag roleFlag, Land land);

    void addNaturalFlag(@NotNull NaturalFlag naturalFlag, Land land);

    void addEntityCapFlag(@NotNull EntityCapFlag entityCapFlag, Land land);

    /**
     * @param roleFlag the flag to remove from the land.
     */
    void removeLandRoleFlag(@NotNull LandRoleFlag roleFlag, @NotNull Land land);

    /**
     * @param naturalFlag the flag to remove from the land.
     */
    void removeLandNaturalFlag(@NotNull LandNaturalFlag naturalFlag, @NotNull Land land);

    /**
     * @param entityCapFlag the flag to remove from the land.
     */
    void removeLandEntityCapFlag(@NotNull LandEntityCapFlag entityCapFlag, @NotNull Land land);

    /**
     * @param flag the flag to update
     */
    void updateLandFlag(@NotNull Flag<?> flag, @NotNull Land land);


}
