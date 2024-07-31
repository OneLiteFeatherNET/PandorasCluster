package net.onelitefeather.pandorascluster.util

import net.onelitefeather.pandorascluster.enums.ChunkRotation
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.land.flag.LandFlagType
import net.onelitefeather.pandorascluster.land.position.dummyHomePosition
import org.bukkit.Material
import org.bukkit.block.BlockFace
import java.util.*

const val IGNORE_CLAIM_LIMIT: Int = -2

val MATERIALS = Material.entries.toTypedArray()
val BLOCK_FACES = BlockFace.entries.toTypedArray()
val CHUNK_ROTATIONS = ChunkRotation.entries.toTypedArray()

val propertyDiscordAvatarUrl: String = System.getProperty("discordAvatarUrl", "https://mc-heads.net/avatar/%s/100")
val propertyDiscordWebhookUrl: String =
    System.getProperty("discordWebhookUrl", "https://discord.com/api/webhooks/%s/%s")

val SERVER_UUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
val EVERYONE: UUID = UUID.fromString("1-1-3-3-7")

val AVAILABLE_CHUNK_ROTATIONS = arrayOf(
    BlockFace.NORTH,
    BlockFace.EAST,
    BlockFace.SOUTH,
    BlockFace.WEST
)

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

val DUMMY_FLAG_ENTITY = LandFlagEntity(
    -1,
    "dummy",
    "dummy",
    0,
    LandFlagType.ENTITY,
    DUMMY_LAND
)



