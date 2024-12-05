package net.onelitefeather.pandorascluster.util

import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.enums.ChunkRotation
import org.bukkit.Material
import org.bukkit.block.BlockFace

val MATERIALS = Material.entries.toTypedArray()
val BLOCK_FACES = BlockFace.entries.toTypedArray()
val CHUNK_ROTATIONS = ChunkRotation.entries.toTypedArray()
val PLUGIN_PREFIX = Component.translatable("prefix")

val propertyDiscordAvatarUrl: String = System.getProperty("discordAvatarUrl", "https://mc-heads.net/avatar/%s/100")
val propertyDiscordWebhookUrl: String =
    System.getProperty("discordWebhookUrl", "https://discord.com/api/webhooks/%s/%s")

val AVAILABLE_CHUNK_ROTATIONS = arrayOf(
    BlockFace.NORTH,
    BlockFace.EAST,
    BlockFace.SOUTH,
    BlockFace.WEST
)



