package net.onelitefeather.pandorascluster.api.service

import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.api.player.LandMember
import net.onelitefeather.pandorascluster.api.player.LandPlayer
import java.util.*

interface LandPlayerService {

    /**
     * @param land the land
     * @param member the member to add
     * @param landRole the land role
     */
    fun addLandMember(land: Land, member: LandPlayer, landRole: LandRole?)

    fun updateLandMember(land: Land, member: LandMember)

    /**
     * @param member the member to remove
     */
    fun removeLandMember(member: LandMember)

    fun getLandMember(land: Land, landPlayer: LandPlayer): LandMember?

    fun getLandPlayers(): List<LandPlayer>

    /**
     * @param uuid the uuid of the player.
     * @param name the name of the player.
     */
    fun createPlayer(uuid: UUID, name: String): Boolean

    /**
     * @param uuid the uuid of the player.
     */
    fun deletePlayer(uuid: UUID)

    /**
     * @param uuid the uuid of the player.
     * @return the land player by the uuid.
     */
    fun getLandPlayer(uuid: UUID): LandPlayer?

    /**
     * @param uuid the uuid of the player.
     * @return true if the player exists
     */
    fun playerExists(uuid: UUID): Boolean

    /**
     * @param landPlayer the player to update
     */
    fun updateLandPlayer(landPlayer: LandPlayer)

    /**
     * @param name the name of the player.
     * @return the land player by the name.
     */
    fun getLandPlayer(name: String): LandPlayer?
}