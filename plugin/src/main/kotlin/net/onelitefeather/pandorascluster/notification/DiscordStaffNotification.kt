package net.onelitefeather.pandorascluster.notification

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.enums.EntityCategory
import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.notification.StaffNotification
import net.onelitefeather.pandorascluster.api.utils.propertyDiscordAvatarUrl
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.util.discord.DiscordWebhook
import net.onelitefeather.pandorascluster.util.discord.embed.EmbedObject
import org.bukkit.plugin.Plugin
import java.awt.Color

class DiscordStaffNotification(
    private val pandorasClusterApi: PandorasClusterApi,
    private val discordWebhook: DiscordWebhook,
    private val plugin: Plugin
) : StaffNotification(pandorasClusterApi), ChunkUtils {

    override fun notifyEntitySpawnLimit(land: Land, entityCategory: EntityCategory) {

        if (!canBeNotified(entityCategory)) return
        val landOwner = land.owner ?: return
        val landOwnerName = landOwner.name

        discordWebhook.username = "PandorasCluster"

        val animalsField =
            EmbedObject.EmbedField("Animals:", getEntityCount(land, EntityCategory.ANIMALS).toString(), true)
        val villagerField =
            EmbedObject.EmbedField("Villager:", getEntityCount(land, EntityCategory.VILLAGER).toString(), true)
        val monsterField =
            EmbedObject.EmbedField("Monster:", getEntityCount(land, EntityCategory.MONSTER).toString(), true)

        val entityCategoryName = entityCategory.name.lowercase().replaceFirstChar { it.uppercase() }
        val embedTitle =
            plugin.config.getString("staff.notification.discord.message")?.format(landOwnerName, entityCategoryName)
                ?: ""

        discordWebhook.addEmbed(
            EmbedObject()
                .setTitle(embedTitle)
                .setColor(Color.RED)
                .setAuthor(landOwnerName, null, propertyDiscordAvatarUrl.format(landOwner.uniqueId))
                .addField(animalsField)
                .addField(villagerField)
                .addField(monsterField)
        )
        discordWebhook.execute()
        updateCooldown(entityCategory, plugin.config.getInt("staff.notification.cooldown"))
    }
}