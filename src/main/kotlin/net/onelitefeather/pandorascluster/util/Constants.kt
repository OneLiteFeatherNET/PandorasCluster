package net.onelitefeather.pandorascluster.util

import net.onelitefeather.pandorascluster.enums.ChunkRotation
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.land.flag.LandFlagType
import net.onelitefeather.pandorascluster.land.position.HomePosition
import org.bukkit.Location
import org.bukkit.block.BlockFace
import java.util.*

class Constants {


    companion object {

        const val PREFIX = "[§aPandorasLand§r]"
        const val PLUGIN_NAME = "PandorasCluster"

        val SERVER_UUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
        val EVERYONE: UUID = UUID.fromString("1-1-3-3-7")

        val DUMMY_LAND = Land(
            -1,
            null,
            (HomePosition.dummyLocation()),
            arrayListOf(),
            arrayListOf(),
            arrayListOf(),
            "world",
            -1, -1
        )

        fun getChunkRotation(name: String): ChunkRotation? {
            return Arrays.stream(CHUNK_ROTATIONS)
                .filter { chunkRotation:
                          ChunkRotation -> chunkRotation.name.equals(name, true) }.findFirst().orElse(null)
        }

        fun getBlockFace(name: String): BlockFace {
            return Arrays.stream(BLOCK_FACES).
            filter{ blockFace: BlockFace -> blockFace.name.equals(name, true) }.findFirst().orElse(null)
        }

        fun getBlockFace(location: Location): BlockFace {
            return Arrays.stream(BLOCK_FACES).
            filter{ blockFace: BlockFace -> blockFace.direction == location.direction }.findFirst().orElse(null)
        }

        val DUMMY_FLAG_ENTITY = LandFlagEntity(
            -1,
            "dummy",
            "dummy",
            0,
            LandFlagType.UNKNOWN,
            DUMMY_LAND
        )


        val BLOCK_FACES = BlockFace.values()

        val CHUNK_ROTATIONS = ChunkRotation.values()
    }
}


