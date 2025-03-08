package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;
import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import org.jetbrains.annotations.NotNull;

public interface LandFlagService {

    /**
     * @param roleFlag the flag
     * @param flagContainer the container for all flags
     */
    void addRoleFlag(@NotNull RoleFlag roleFlag, FlagContainer flagContainer);

    void updateRoleFlag(@NotNull LandRoleFlag roleFlag);

    /**
     * @param roleFlag the flag to remove from the land.
     */
    void removeRoleFlag(@NotNull LandRoleFlag roleFlag, @NotNull FlagContainer flagContainer);

    void addNaturalFlag(@NotNull NaturalFlag naturalFlag, FlagContainer flagContainer);

    /**
     * @param entityCapFlag the flag to update
     */
    void updateNaturalCapFlag(@NotNull LandNaturalFlag naturalFlag);

    /**
     * @param naturalFlag the flag to remove from the land.
     */
    void removeNaturalFlag(@NotNull LandNaturalFlag naturalFlag, @NotNull FlagContainer flagContainer);


    void addEntityCapFlag(@NotNull EntityCapFlag entityCapFlag, FlagContainer flagContainer);

    /**
     * @param entityCapFlag the flag to update
     */
    void updateEntityCapFlag(@NotNull LandEntityCapFlag entityCapFlag);

    /**
     * @param entityCapFlag the flag to remove from the land.
     */
    void removeEntityCapFlag(@NotNull LandEntityCapFlag entityCapFlag, @NotNull FlagContainer flagContainer);
}
