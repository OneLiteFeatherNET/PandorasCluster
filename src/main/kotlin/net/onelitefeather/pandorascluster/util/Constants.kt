package net.onelitefeather.pandorascluster.util

import net.onelitefeather.pandorascluster.enums.ChunkRotation
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.land.flag.LandFlagType
import net.onelitefeather.pandorascluster.land.position.dummyHomePosition
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import java.util.*

const val PREFIX = "[§aPandorasLand§r]"
const val PLUGIN_NAME = "PandorasCluster"
const val CHUNK_LENGTH = 16

const val IGNORE_CLAIM_LIMIT: Int = -2
val SERVER_UUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
val EVERYONE: UUID = UUID.fromString("1-1-3-3-7")

val DUMMY_LAND = Land(
    -1,
    null,
    dummyHomePosition(),
    arrayListOf(),
    arrayListOf(),
    arrayListOf(),
    "world",
    -1, -1
)

fun getChunkRotation(name: String): ChunkRotation? {
    return CHUNK_ROTATIONS.firstOrNull { chunkRotation:
                                                  ChunkRotation ->
        chunkRotation.name.equals(name, true)
    }
}

fun getBlockFace(name: String): BlockFace? {
    return BLOCK_FACES.firstOrNull { blockFace -> blockFace.name.equals(name, true) }
}

fun getBlockFace(location: Location): BlockFace? {
    return BLOCK_FACES.firstOrNull { blockFace ->  blockFace.direction == location.direction}
}

val AVAILABLE_CHUNK_ROTATIONS = arrayOf(
    BlockFace.NORTH,
    BlockFace.EAST,
    BlockFace.SOUTH,
    BlockFace.WEST)

val DUMMY_FLAG_ENTITY = LandFlagEntity(
    -1,
    "dummy",
    "dummy",
    0,
    LandFlagType.ENTITY,
    DUMMY_LAND
)

val MATERIALS = Material.values()
val BLOCK_FACES = BlockFace.values()
val CHUNK_ROTATIONS = ChunkRotation.values()

fun getChunkRotation(facing: BlockFace) : ChunkRotation? {
    return CHUNK_ROTATIONS.firstOrNull { it.name == facing.name }
}

