package net.onelitefeather.pandorascluster.api.service

import net.onelitefeather.pandorascluster.api.enum.LandRole
import net.onelitefeather.pandorascluster.api.models.*
import java.util.*

interface DatabaseStorageService {

    /**
     * @param homePosition the home position of the land
     * @param ownerId the new owner uuid
     */
    fun updateLandHome(homePosition: HomePosition, ownerId: UUID)

    /**
     * @param land the land
     * @param member the member to add
     * @param landRole the land role
     */
    fun addLandMember(land: Land, member: LandPlayer, landRole: LandRole?)

    /**
     * @param land the land to change the owner.
     * @param landPlayer the new owner of the land.
     */
    fun setLandOwner(land: Land, landPlayer: LandPlayer)

    /**
     * Add an Item for the use flag.
     * @param land the land to add the use material
     * @param material the name of the material
     */
    fun addUseMaterial(land: Land, material: String)

    /**
     * @param landFlag the landFlag
     * @param land the land
     */
    fun getLandFlag(landFlag: LandFlag, land: Land): LandFlagEntity?

    /**
     * Remove an Item of the use flag.
     * @param land the land to remove the use material
     * @param material the name of the material
     */
    fun removeUseMaterial(land: Land, material: String)

    /**
     * @param landFlag the landflag
     * @param value the value of the flag
     * @param land the land to update or add the flag
     */
    fun updateLandFlag(landFlag: LandFlag, value: String, land: Land)

    /**
     * @param land the land to update
     */
    fun updateLand(land: Land)

    /**
     * @param chunk the chunk to claim
     * @param land the land to add the chunk
     */
    fun addChunkPlaceholder(chunk: Chunk, land: Land?)

    /**
     * @param owner the owner of the land.
     * @param player the bukkit player
     * @param chunk the first claimed chunk
     */
    fun createLand(owner: LandPlayer, player: Player, chunk: Chunk)

    /**
     * @param land the land to unclaim.
     */
    fun unclaimLand(land: Land)

    /**
     * @param chunkPlaceholder the chunk to remove from the land.
     */
    fun removeChunkPlaceholder(chunkPlaceholder: ChunkPlaceholder)

    /**
     * @param landFlagEntity the flag to remove from the land.
     */
    fun removeLandFlag(landFlagEntity: LandFlagEntity)

    /**
     * @param member the member to remove
     */
    fun removeLandMember(member: LandMember)
}