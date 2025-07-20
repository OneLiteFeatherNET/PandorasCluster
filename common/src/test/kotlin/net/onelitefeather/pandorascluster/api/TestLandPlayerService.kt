package net.onelitefeather.pandorascluster.api

import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.api.service.LandPlayerService
import net.onelitefeather.pandorascluster.api.service.LandService
import org.junit.Test
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestLandPlayerService {

    private val api: PandorasClusterApi = TestPandorasClusterApiImpl()
    private val landService: LandService = api.getLandService()
    private val landPlayerService: LandPlayerService = api.getLandPlayerService()

    private val testLandService = TestLandService()

    private val uuid = UUID.fromString("df7c66a6-7876-44c2-9abd-9bd33f7d3d9e")

    @Test
    fun testCreateLandPlayer() {
        val playerName = "theShadowsDust"
        if(landPlayerService.playerExists(uuid)) return
        assertTrue { landPlayerService.createPlayer(uuid, playerName) }
    }

    @Test
    fun testUpdateLandPlayer() {
        val landPlayer = landPlayerService.getLandPlayer(uuid)
        assertNotNull(landPlayer)
        landPlayerService.updateLandPlayer(landPlayer.copy(name = "theEvilReaper"))
    }

    @Test
    fun testAddLandMember() {
        testLandService.testLandCreation()
        val land = landService.getLand(mainChunk)
        assertNotNull(land)

        val landPlayer = landPlayerService.getLandPlayer(uuid)
        assertNotNull(landPlayer)

        landPlayerService.addLandMember(land, landPlayer, LandRole.MEMBER)
    }

    @Test
    fun testUpdateLandMember() {
        testLandService.testLandCreation()
        val land = landService.getLand(mainChunk)
        assertNotNull(land)

        val member = landPlayerService.getLandMember(land, uuid)
        assertNotNull(member)

        landPlayerService.updateLandMember(land, member.copy(role = LandRole.TRUSTED))
    }

    @Test
    fun testRemoveLandMember() {
        val land = landService.getLand(mainChunk)
        assertNotNull(land)

        val member = landPlayerService.getLandMember(land, uuid)
        assertNotNull(member)
        landPlayerService.removeLandMember(member)
    }

    @Test
    fun testDeleteLandPlayer() {
        landPlayerService.deletePlayer(uuid)
    }
}