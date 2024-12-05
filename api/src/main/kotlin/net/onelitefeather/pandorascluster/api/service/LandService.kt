package net.onelitefeather.pandorascluster.api.service

import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk
import net.onelitefeather.pandorascluster.api.player.LandPlayer
import net.onelitefeather.pandorascluster.api.position.HomePosition
import java.util.*

interface LandService {

    fun getLands(): List<Land>

    /**
     * @param homePosition the home position of the land
     * @param ownerId the new owner uuid
     */
    fun updateLandHome(homePosition: HomePosition, ownerId: UUID)

    /**
     * @param land the land to update
     */
    fun updateLand(land: Land)

    /**
     * @param chunk the chunk to claim
     * @param land the land to add the chunk
     */
    fun addClaimedChunk(chunk: ClaimedChunk, land: Land?)

    /**
     * @param owner the owner of the land.
     * @param home the home position of the land
     * @param world the name of the world.
     * @param chunk the first claimed chunk
     */
    fun createLand(owner: LandPlayer, home: HomePosition, chunk: ClaimedChunk, world: String): Land?

    /**
     * @param land the land to unclaim.
     */
    fun unclaimLand(land: Land)

    /**
     * @param claimedChunk the chunk to remove from the land.
     */
    fun removeClaimedChunk(chunkIndex: Long): Boolean

    fun getLand(chunkIndex: Long): Land?

    /**
     *
     * @param chunk the given chunk
     * @return the cached land.
     */
    fun getLand(chunk: ClaimedChunk): Land?

//    fun getLand(chunk: ClaimedChunk): Land?

    fun getLand(owner: LandPlayer): Land?

    fun isChunkClaimed(chunk: ClaimedChunk): Boolean

    fun isChunkClaimed(chunkIndex: Long): Boolean

    fun hasPlayerLand(player: LandPlayer): Boolean

    fun getClaimedChunk(chunkIndex: Long): ClaimedChunk?
}