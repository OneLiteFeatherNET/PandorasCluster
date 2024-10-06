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
    fun testLandFlagService() {

        testLandService.testLandCreation()
        var land = api.getLandService().getLand(mainChunk)
        assertNotNull(land)
        
        landFlagService.addLandFlag(LandFlag.POTION_SPLASH, LandRole.MEMBER, land)
        landFlagService.addLandFlag(LandFlag.ENTITY_LEASH, LandRole.VISITOR, land)
        landFlagService.addLandFlag(LandFlag.VILLAGER_INTERACT, LandRole.ADMIN, land)

        land = api.getLandService().getLand(mainChunk)
        assertNotNull(land)

        val landFlag = landFlagService.getLandFlag(LandFlag.ENTITY_LEASH, land)
        assertNotNull(landFlag)
        landFlagService.removeLandFlag(landFlag, land)

        val flag = land.getFlag(LandFlag.POTION_SPLASH).copy(role = LandRole.MEMBER)
        assertNotNull(flag)
        landFlagService.updateLandFlag(flag, land)
    }
}