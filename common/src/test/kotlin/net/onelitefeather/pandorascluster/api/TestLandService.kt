package net.onelitefeather.pandorascluster.api

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk
import net.onelitefeather.pandorascluster.api.service.LandService
import org.junit.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestLandService {

    private val api: PandorasClusterApi = TestPandorasClusterApiImpl()
    private val landService: LandService = api.getLandService()

    @Test
    fun testCreateLandOwner() {
        if (api.getLandPlayerService().playerExists(landOwnerUUID)) return
        assertTrue(api.getLandPlayerService().createPlayer(landOwnerUUID, "theShadowsDust"))
    }

    @Test
    fun testLandCreation() {
        val landPlayer = api.getLandPlayerService().getLandPlayer(landOwnerUUID)
        assertNotNull(landPlayer)

        val land = landService.createLand(landPlayer, landHome, mainChunk, "world")
        assertNotNull(land)
    }

    @Test
    fun testUpdateLandHome() {

        val owner = api.getLandPlayerService().getLandPlayer(landOwnerUUID)
        assertNotNull(owner)

        val land = landService.getLand(owner)
        assertNotNull(land)

        landService.updateLandHome(updatedHome.copy(land.home?.id), landOwnerUUID)
        assertNotEquals(updatedHome, land.home)
    }

    @Test
    fun testisChunkClaimed() {
        assertTrue(landService.isChunkClaimed(mainChunk))
    }

    @Test
    fun testhasPlayerLand() {
        val owner = api.getLandPlayerService().getLandPlayer(landOwnerUUID)
        assertNotNull(owner)
        assertTrue(landService.hasPlayerLand(owner))
    }

    @Test
    fun testAddAndRemoveChunk() {

        val owner = api.getLandPlayerService().getLandPlayer(landOwnerUUID)
        assertNotNull(owner)

        val land = landService.getLand(owner)
        assertNotNull(land)

        val claimedChunk = ClaimedChunk(null, -random.nextLong(randomBound))
        landService.addClaimedChunk(claimedChunk, land)

        val chunk = landService.getClaimedChunk(claimedChunk.chunkIndex)
        assertNotNull(chunk)
        assertTrue(landService.removeClaimedChunk(chunk.chunkIndex))
    }

    @Test
    fun testgetLand() {


        val land = landService.getLand(mainChunk)
        println("land = $land")
        assertNotNull(land)

        val ownerByName = api.getLandPlayerService().getLandPlayer("theShadowsDust")
        assertNotNull(ownerByName)
        assertNotNull(landService.getLand(ownerByName))
    }

    @Test
    fun testUnclaimLand() {
        val land = landService.getLand(mainChunk)
        assertNotNull(land)
        landService.unclaimLand(land)
    }
}