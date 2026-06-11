package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import net.onelitefeather.pandorascluster.api.service.result.AddLandFlagResult;
import net.onelitefeather.pandorascluster.api.service.result.RemoveLandFlagResult;
import org.jetbrains.annotations.NotNull;

public interface LandFlagService {

    /**
     * @param roleFlag the flag
     * @param flagContainer the container for all flags
     */
    AddLandFlagResult addRoleFlag(@NotNull LandRoleFlag roleFlag, FlagContainer flagContainer);

    void updateRoleFlag(@NotNull LandRoleFlag roleFlag);

    /**
     * @param roleFlag the flag to remove from the land.
     */
    RemoveLandFlagResult removeRoleFlag(@NotNull LandRoleFlag roleFlag, @NotNull FlagContainer flagContainer);

    AddLandFlagResult addNaturalFlag(@NotNull LandNaturalFlag naturalFlag, FlagContainer flagContainer);

    /**
     * @param naturalFlag the flag to update
     */
    void updateNaturalCapFlag(@NotNull LandNaturalFlag naturalFlag);

    /**
     * @param naturalFlag the flag to remove from the land.
     */
    RemoveLandFlagResult removeNaturalFlag(@NotNull LandNaturalFlag naturalFlag, @NotNull FlagContainer flagContainer);


    AddLandFlagResult addEntityCapFlag(@NotNull LandEntityCapFlag entityCapFlag, FlagContainer flagContainer);

    /**
     * @param entityCapFlag the flag to update
     */
    void updateEntityCapFlag(@NotNull LandEntityCapFlag entityCapFlag);

    /**
     * @param entityCapFlag the flag to remove from the land.
     */
    RemoveLandFlagResult removeEntityCapFlag(@NotNull LandEntityCapFlag entityCapFlag, @NotNull FlagContainer flagContainer);
}
