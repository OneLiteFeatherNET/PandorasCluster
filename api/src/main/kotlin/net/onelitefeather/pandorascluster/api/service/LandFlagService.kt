package net.onelitefeather.pandorascluster.api.service

import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.api.land.flag.FlagRoleAttachment
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag

interface LandFlagService {

    /**
     * @param landFlag the landflag
     * @param land the land to add the flag
     */
    fun addLandFlag(landFlag: LandFlag, role: LandRole?, land: Land)

    /**
     * @param flag the flag to update
     */
    fun updateLandFlag(flag: FlagRoleAttachment, land: Land)

    /**
     * @param flag the flag to remove from the land.
     */
    fun removeLandFlag(flag: FlagRoleAttachment, land: Land)
}