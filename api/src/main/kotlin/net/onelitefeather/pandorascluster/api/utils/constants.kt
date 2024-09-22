package net.onelitefeather.pandorascluster.api.utils

import java.util.*
import java.util.logging.Logger

// Both varibles are getting used in later releases
val SERVER_UUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
val EVERYONE: UUID = UUID.fromString("1-1-3-3-7")
val LOGGER: Logger = Logger.getLogger("PandorasCluster")

const val IGNORE_CLAIM_LIMIT: Int = -2
val propertyDiscordAvatarUrl: String = System.getProperty("discordAvatarUrl", "https://mc-heads.net/avatar/%s/100")
val propertyDiscordWebhookUrl: String =
    System.getProperty("discordWebhookUrl", "https://discord.com/api/webhooks/%s/%s")