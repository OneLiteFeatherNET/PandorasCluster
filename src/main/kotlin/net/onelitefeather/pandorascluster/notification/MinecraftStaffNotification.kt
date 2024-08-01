package net.onelitefeather.pandorascluster.notification

import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.EntityCategory
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.land.Land

class MinecraftStaffNotification(private val pandorasClusterApi: PandorasClusterApi) :
    StaffNotification(pandorasClusterApi), ChunkUtils {

    override fun notifyEntitySpawnLimit(land: Land, entityCategory: EntityCategory) {

        if (!canBeNotified(entityCategory)) return

        val players = pandorasClusterApi.getPlugin().server.onlinePlayers
        val landOwner = land.owner ?: return

        players.filter { it.hasPermission(getPermission()) }.forEach { player ->

            val ownerName = Component.text(landOwner.name!!)
            val entityCategoryName = Component.text(entityCategory.name.lowercase().replaceFirstChar { it.uppercase() })
            val hoverMessage = Component.translatable("staff.notification.mob.limit.reached.hover").arguments(
                pandorasClusterApi.pluginPrefix(),
                Component.text(land.id!!),
                getHighlightColor(land, EntityCategory.ANIMALS),
                getHighlightColor(land, EntityCategory.VILLAGER),
                getHighlightColor(land, EntityCategory.MONSTER),
                Component.translatable("staff.notification.mob.limit.reached").arguments(ownerName, entityCategoryName)
            )

            player.sendMessage(hoverMessage)
        }

        updateCooldown(entityCategory, cooldown())
    }

    private fun getHighlightColor(land: Land, entityCategory: EntityCategory): Component {

        val count = getEntityCount(land, entityCategory)
        val limit = getEntityLimit(land, entityCategory)

        val messageKey = if(count >= limit) "staff.notification.mob.count.red" else "staff.notification.mob.count.green"
        return Component.translatable(messageKey)
    }

    private fun getPermission() = "pandorascluster.staff.notify"
}