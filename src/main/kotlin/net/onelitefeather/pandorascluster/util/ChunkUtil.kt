package net.onelitefeather.pandorascluster.util

import net.onelitefeather.pandorascluster.api.MobCapType
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.block.Block
import org.bukkit.entity.AbstractVillager
import org.bukkit.entity.Animals
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.entity.SpawnCategory

fun hasSameOwner(it: Land, claimedLand: Land) = it.owner == claimedLand.owner

fun getEntityLimit(land: Land, mobCapType: MobCapType): Int {

    if(mobCapType == MobCapType.ANIMALS) {
        return land.getLandFlag(LandFlag.ANIMAL_CAP).getValue() ?: 0
    }

    if(mobCapType == MobCapType.MONSTER) {
        return land.getLandFlag(LandFlag.MONSTER_CAP).getValue() ?: 0
    }

    if(mobCapType == MobCapType.VILLAGER) {
        return land.getLandFlag(LandFlag.VILLAGER_CAP).getValue() ?: 0
    }

    return 0
}

fun getEntityCount(land: Land, mobCapType: MobCapType): Int {

    var count = 0
    val world = Bukkit.getWorld(land.world) ?: return 0

    land.chunks.forEach {
        val bukkitChunk = world.getChunkAt(it.chunkIndex)
        count += when(mobCapType) {
            MobCapType.ANIMALS -> bukkitChunk.entities.filterIsInstance<Animals>().size
            MobCapType.MONSTER -> bukkitChunk.entities.filterIsInstance<Monster>().size
            MobCapType.VILLAGER -> bukkitChunk.entities.filterIsInstance<AbstractVillager>().size
        }
    }

    return count
}


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