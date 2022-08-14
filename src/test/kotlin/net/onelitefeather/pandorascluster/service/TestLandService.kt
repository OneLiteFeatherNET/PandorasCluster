package net.onelitefeather.pandorascluster.service

import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.world.World
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform
import com.sk89q.worldguard.protection.managers.RegionManager
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import com.sk89q.worldguard.protection.regions.RegionContainer
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.bukkit.Chunk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestLandService {

    @Test
    fun testCheckWorldGuardRegion() {

        val mockChunk = mockk<Chunk>()
        val world = mockk<World>()

        val bukkitWorld = mockk<org.bukkit.World>()

        val regionManager = mockk<RegionManager>()
        val regionContainer = mockk<RegionContainer>()
        val platform = mockk<WorldGuardPlatform>()
        val testRegions = mockk<Map<String, ProtectedRegion>>()
        val protectedRegions = mockk<List<ProtectedRegion>>()


        every { mockChunk.x } returns -1
        every { mockChunk.z } returns 1
        every { platform.regionContainer } returns regionContainer
        every { platform.regionContainer.get(world) } returns regionManager
        every { regionManager.regions } returns testRegions
        every { mockChunk.world.maxHeight } returns 320

        val minChunkX = mockChunk.x shl 4
        val minChunkZ = mockChunk.z shl 4
        val maxChunkX = minChunkX + 15
        val maxChunkZ = minChunkZ + 15

        val region = spyk<ProtectedCuboidRegion>(ProtectedCuboidRegion(
            "check_wg_overlaps",
            BlockVector3.at(minChunkX, 0, minChunkZ),
            BlockVector3.at(maxChunkX, mockChunk.world.maxHeight, maxChunkZ)))
        val region2 = spyk<ProtectedCuboidRegion>(ProtectedCuboidRegion(
            "check_wg_overlaps",
            BlockVector3.at(minChunkX, 0, minChunkZ),
            BlockVector3.at(maxChunkX, mockChunk.world.maxHeight, maxChunkZ)))

        every { testRegions.values } returns listOf(region2)

        val regions = regionManager.regions?.values
        Assertions.assertTrue(region.getIntersectingRegions(regions).isNotEmpty())
        verify { region.getIntersectingRegions(regions) }

    }
}