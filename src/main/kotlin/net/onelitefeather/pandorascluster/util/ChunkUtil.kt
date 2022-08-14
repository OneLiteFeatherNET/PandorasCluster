package net.onelitefeather.pandorascluster.util

import net.onelitefeather.pandorascluster.land.Land
import org.bukkit.Chunk
import org.bukkit.entity.Player

fun hasSameOwner(it: Land, claimedLand: Land) = it.owner == claimedLand.owner

/**
 * Gets the chunk index of chunk coordinates.
 * <p>
 * Used when you want to store a chunk somewhere without using a reference to the whole object
 * (as this can lead to memory leaks).
 *
 * @param chunkX the chunk X
 * @param chunkZ the chunk Z
 * @return a number storing the chunk X and Z
 */
fun getChunkIndex(chunkX: Int, chunkZ: Int): Long = chunkX.toLong() shl 32 or (chunkZ.toLong() and 0xffffffffL)

fun getChunkIndex(chunk: Chunk): Long = getChunkIndex(chunk.x, chunk.z)

/**
 * Converts a chunk index to its chunk X position.
 *
 * @param index the chunk index computed by [.getChunkIndex]
 * @return the chunk X based on the index
 */
fun getChunkCoordX(index: Long): Int = (index shr 32).toInt()

/**
 * Converts a chunk index to its chunk Z position.
 *
 * @param index the chunk index computed by [.getChunkIndex]
 * @return the chunk Z based on the index
 */
fun getChunkCoordZ(index: Long): Int = index.toInt()

fun getPlayersInChunk(chunk: Chunk): List<Player> = chunk.entities.filterIsInstance<Player>()