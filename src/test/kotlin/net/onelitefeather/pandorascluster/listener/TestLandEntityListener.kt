package net.onelitefeather.pandorascluster.listener

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.command.commands.ClaimCommand
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.service.LandService
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Player
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.*

class TestLandEntityListener {


    @Disabled
    @Test
    fun `test execute`() {
        val pandorasClusterApi = mockk<PandorasClusterApi>()
        val mockLandPlayer = mockk<LandPlayer>()
        val mockChunk = mockk<Chunk>()
        val mockLocation = mockk<Location>()
        val player = mockk<Player>()
        val landService = mockk<LandService>()

        every { landService.checkWorldGuardRegion(mockChunk) } returns true
        every { pandorasClusterApi.getLandPlayer(any<UUID>()) } returns mockLandPlayer
        every { pandorasClusterApi.getLandService() } answers  { landService }
        every { player.chunk } returns mockChunk
        every { player.uniqueId } returns UUID.randomUUID()
        every { player.location } returns mockLocation
        every { player.sendMessage(any<Component>()) } answers {}
        val claimCommand = ClaimCommand(pandorasClusterApi)
        claimCommand.execute(player)
        verify { player.sendMessage(any<Component>()) }
    }


}
