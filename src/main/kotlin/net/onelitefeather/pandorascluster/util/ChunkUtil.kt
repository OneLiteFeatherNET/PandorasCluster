package net.onelitefeather.pandorascluster.util

import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.api.EntityCategory
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.block.Block
import org.bukkit.entity.AbstractVillager
import org.bukkit.entity.Animals
import org.bukkit.entity.Monster
import org.bukkit.entity.Player

fun canEnterLand(player: Player, land: Land): Boolean {
    return Permission.LAND_ENTRY_DENIED.hasPermission(player) || !land.isBanned(player.uniqueId)
}

fun hasSameOwner(it: Land, claimedLand: Land) = it.owner == claimedLand.owner

fun getEntityLimit(land: Land, entityCategory: EntityCategory): Int {

    if(entityCategory == EntityCategory.ANIMALS) {
        return land.getLandFlag(LandFlag.ANIMAL_CAP).getValue() ?: 0
    }

    if(entityCategory == EntityCategory.MONSTER) {
        return land.getLandFlag(LandFlag.MONSTER_CAP).getValue() ?: 0
    }

    if(entityCategory == EntityCategory.VILLAGER) {
        return land.getLandFlag(LandFlag.VILLAGER_CAP).getValue() ?: 0
    }

    return 0
}

fun getEntityCount(land: Land, entityCategory: EntityCategory): Int {

    var count = 0
    val world = Bukkit.getWorld(land.world) ?: return 0

    land.chunks.forEach {
        val bukkitChunk = world.getChunkAt(it.chunkIndex)
        count += when(entityCategory) {
            EntityCategory.ANIMALS -> bukkitChunk.entities.filterIsInstance<Animals>().size
            EntityCategory.MONSTER -> bukkitChunk.entities.filterIsInstance<Monster>().size
            EntityCategory.VILLAGER -> bukkitChunk.entities.filterIsInstance<AbstractVillager>().size
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