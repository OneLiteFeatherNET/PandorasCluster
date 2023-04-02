package net.onelitefeather.pandorascluster.util

import net.onelitefeather.pandorascluster.land.Land
import org.bukkit.Chunk
import org.bukkit.block.Block
import org.bukkit.entity.Player

fun hasSameOwner(it: Land, claimedLand: Land) = it.owner == claimedLand.owner

fun chunkCorners(chunk: Chunk): List<Block> {
    val world = chunk.world
    val chunkX = chunk.x
    val chunkZ = chunk.z
    return listOf(
        world.getHighestBlockAt(chunkX * 16, chunkZ * 16),
        world.getHighestBlockAt(chunkX * 16 + 15, chunkZ * 16),
        world.getHighestBlockAt(chunkX * 16, chunkZ * 16 + 15),
        world.getHighestBlockAt(chunkX * 16 + 15, chunkZ * 16 + 15)
    )
}

fun Chunk.corners(): List<Block> {
    val world = this.world
    val chunkX = this.x
    val chunkZ = this.z
    return listOf(
        world.getHighestBlockAt(chunkX * 16, chunkZ * 16),
        world.getHighestBlockAt(chunkX * 16 + 15, chunkZ * 16),
        world.getHighestBlockAt(chunkX * 16, chunkZ * 16 + 15),
        world.getHighestBlockAt(chunkX * 16 + 15, chunkZ * 16 + 15)
    )
}

fun getPlayersInChunk(chunk: Chunk): List<Player> = chunk.entities.filterIsInstance<Player>()