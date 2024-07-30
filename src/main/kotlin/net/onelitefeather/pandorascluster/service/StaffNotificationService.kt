package net.onelitefeather.pandorascluster.service

import net.onelitefeather.pandorascluster.api.EntityCategory
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.notification.DiscordStaffNotification
import net.onelitefeather.pandorascluster.notification.MinecraftStaffNotification
import net.onelitefeather.pandorascluster.notification.StaffNotification
import net.onelitefeather.pandorascluster.util.discord.DiscordWebhook

class StaffNotificationService(private val pandorasClusterApi: PandorasClusterApi) {

    private val staffNotifications: MutableList<StaffNotification> = arrayListOf()
    private var discordWebhook: DiscordWebhook? = null

    init {
        addStaffNotification(MinecraftStaffNotification(pandorasClusterApi))

        this.discordWebhook = buildDiscordWebhook()
        if(this.discordWebhook != null) {
            addStaffNotification(DiscordStaffNotification(pandorasClusterApi, this.discordWebhook!!))
        }
    }

    fun addStaffNotification(staffNotification: StaffNotification) {
        staffNotifications.add(staffNotification)
    }

    fun notify(land: Land, category: EntityCategory) {
        staffNotifications.forEach { it.notifyEntitySpawnLimit(land, category) }
    }

    private fun buildDiscordWebhook(): DiscordWebhook? {
        val useDiscordStaffNotification =
            pandorasClusterApi.getPlugin().config.getBoolean("staff.notification.discord.enabled")
        val token = pandorasClusterApi.getPlugin().config.getString("staff.notification.discord.token", "")!!
        val tokenId = pandorasClusterApi.getPlugin().config.getString("staff.notification.discord.tokenId", "")!!

        if (!useDiscordStaffNotification || token.isEmpty() || tokenId.isEmpty()) return null

        return DiscordWebhook(token, tokenId)
    }
}