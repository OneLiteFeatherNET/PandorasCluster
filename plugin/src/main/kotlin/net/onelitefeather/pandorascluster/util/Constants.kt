package net.onelitefeather.pandorascluster.util

import net.onelitefeather.pandorascluster.api.enums.ChunkRotation
import org.bukkit.Material
import org.bukkit.block.BlockFace

val MATERIALS = Material.entries.toTypedArray()
val BLOCK_FACES = BlockFace.entries.toTypedArray()
val CHUNK_ROTATIONS = ChunkRotation.entries.toTypedArray()

val AVAILABLE_CHUNK_ROTATIONS = arrayOf(
    BlockFace.NORTH,
    BlockFace.EAST,
    BlockFace.SOUTH,
    BlockFace.WEST
)



