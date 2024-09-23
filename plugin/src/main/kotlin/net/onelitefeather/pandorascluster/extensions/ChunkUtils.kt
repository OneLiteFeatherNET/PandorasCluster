package net.onelitefeather.pandorascluster.extensions

import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk
import net.onelitefeather.pandorascluster.api.enums.ChunkRotation
import net.onelitefeather.pandorascluster.api.enums.EntityCategory
import net.onelitefeather.pandorascluster.api.enums.Permission
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.util.BLOCK_FACES
import net.onelitefeather.pandorascluster.util.CHUNK_ROTATIONS
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.AbstractVillager
import org.bukkit.entity.Animals
import org.bukkit.entity.Monster
import org.bukkit.entity.Player

interface ChunkUtils {

    fun canEnterLand(player: Player, land: Land): Boolean {
        return player.hasPermission(Permission.LAND_ENTRY_DENIED.permissionNode) || !land.isBanned(player.uniqueId)
    }

    fun hasSameOwner(it: Land, claimedLand: Land) = it.owner == claimedLand.owner

    fun getEntityLimit(land: Land, entityCategory: EntityCategory): Int {

        if(entityCategory == EntityCategory.ANIMALS) {
            return land.getFlag(LandFlag.ANIMAL_CAP).getValue() ?: 0
        }

        if(entityCategory == EntityCategory.MONSTER) {
            return land.getFlag(LandFlag.MONSTER_CAP).getValue() ?: 0
        }

        if(entityCategory == EntityCategory.VILLAGER) {
            return land.getFlag(LandFlag.VILLAGER_CAP).getValue() ?: 0
        }

        return 0
    }

    fun toClaimedChunk(chunk: Chunk): ClaimedChunk = ClaimedChunk(null, chunk.chunkKey)

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

    fun getPlayersInChunk(chunk: Chunk): List<Player> = chunk.entities.filterIsInstance<Player>()

    fun chunkLength() = 16

    fun getChunkRotation(facing: BlockFace): ChunkRotation? {
        return CHUNK_ROTATIONS.firstOrNull { it.name == facing.name }
    }

    fun getBlockFace(location: Location): BlockFace? {
        return BLOCK_FACES.firstOrNull { it.direction == location.direction }
    }

}