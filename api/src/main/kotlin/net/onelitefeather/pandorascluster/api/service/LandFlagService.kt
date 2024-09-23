package net.onelitefeather.pandorascluster.api.service

import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.api.land.flag.FlagRoleAttachment
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag

interface LandFlagService {

    /**
     * @param landFlag the landflag
     * @param value the value of the flag
     * @param land the land to add the flag
     */
    fun addLandFlag(landFlag: LandFlag, value: String, role: LandRole?, land: Land)

    /**
     * @param flag the flag to update
     */
    fun updateLandFlag(flag: FlagRoleAttachment)

    /**
     * @param landFlagProperty the flag to remove from the land.
     */
    fun removeLandFlag(landFlagProperty: FlagRoleAttachment)

    fun getLandFlag(landFlag: LandFlag, land: Land): FlagRoleAttachment? = land.getFlag(landFlag)
}