package net.onelitefeather.pandorascluster.notification

import net.onelitefeather.pandorascluster.api.EntityCategory
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.util.discord.DiscordWebhook
import net.onelitefeather.pandorascluster.util.discord.embed.EmbedObject
import net.onelitefeather.pandorascluster.util.propertyDiscordAvatarUrl
import java.awt.Color

class DiscordStaffNotification(private val pandorasClusterApi: PandorasClusterApi,
    private val discordWebhook: DiscordWebhook) : StaffNotification(pandorasClusterApi), ChunkUtils {

    override fun notifyEntitySpawnLimit(land: Land, entityCategory: EntityCategory) {

        if(!canBeNotified(entityCategory)) return
        val landOwner = land.owner ?: return
        val landOwnerName = landOwner.name ?: return

        discordWebhook.username = "PandorasCluster"

        val animalsField = EmbedObject.EmbedField("Animals:", getEntityCount(land, EntityCategory.ANIMALS).toString(), true)
        val villagerField = EmbedObject.EmbedField("Villager:", getEntityCount(land, EntityCategory.VILLAGER).toString(), true)
        val monsterField = EmbedObject.EmbedField("Monster:", getEntityCount(land, EntityCategory.MONSTER).toString(), true)

        val entityCategoryName = entityCategory.name.lowercase().replaceFirstChar { it.uppercase() }
        val embedTitle = pandorasClusterApi.getPlugin().config.getString("staff.notification.discord.message")?.format(landOwnerName, entityCategoryName) ?: ""

        discordWebhook.addEmbed(EmbedObject()
            .setTitle(embedTitle)
            .setColor(Color.RED)
            .setAuthor(landOwnerName, null, propertyDiscordAvatarUrl.format(landOwner.getUniqueId()))
            .addField(animalsField)
            .addField(villagerField)
            .addField(monsterField))
        discordWebhook.execute()
        updateCooldown(entityCategory, cooldown())
    }
}