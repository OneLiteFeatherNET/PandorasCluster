package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.flag.Flag;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import org.jetbrains.annotations.NotNull;

public interface LandFlagService {

    /**
     * @param roleFlag the flag
     * @param landArea the land area to add the flag
     */
    void addRoleFlag(@NotNull RoleFlag roleFlag, LandArea landArea);

    /**
     * @param flag the flag to update
     */
    void updateLandFlag(@NotNull Flag<?> flag, @NotNull Land land);

    /**
     * @param flag the flag to remove from the land.
     */
    void removeLandFlag(@NotNull Flag<?> flag, @NotNull Land land);

}
