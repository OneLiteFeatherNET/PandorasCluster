package net.onelitefeather.pandorascluster.api

import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.api.service.LandFlagService
import net.onelitefeather.pandorascluster.database.service.DatabaseLandFlagService
import org.junit.Test
import kotlin.test.assertNotNull

class TestLandFlagService {

    private val api: PandorasClusterApi = TestPandorasClusterApiImpl()
    private val landFlagService: LandFlagService = DatabaseLandFlagService(api.getDatabaseService(), api.getLandService())
    private val testLandService: TestLandService = TestLandService()

    @Test
    fun testAddLandFlag() {
        testLandService.testLandCreation()
        val land = api.getLandService().getLand(mainChunk)
        assertNotNull(land)
        landFlagService.addLandFlag(LandFlag.POTION_SPLASH, LandRole.MEMBER, land)
        landFlagService.addLandFlag(LandFlag.ENTITY_LEASH, LandRole.VISITOR, land)
        landFlagService.addLandFlag(LandFlag.VILLAGER_INTERACT, LandRole.ADMIN, land)
    }

    @Test
    fun testRemoveLandFlag() {

        testLandService.testLandCreation()
        val land = api.getLandService().getLand(mainChunk)
        assertNotNull(land)

        val landFlag = land.getFlag(LandFlag.ENTITY_LEASH)
        assertNotNull(landFlag)
        landFlagService.removeLandFlag(landFlag, land)
    }

    @Test
    fun testUpdateLandFlag() {

        testLandService.testLandCreation()
        val land = api.getLandService().getLand(mainChunk)
        assertNotNull(land)

        val flag = land.getFlag(LandFlag.POTION_SPLASH)
        assertNotNull(flag)
        landFlagService.updateLandFlag(flag.copy(role = LandRole.ADMIN), land)
    }
}