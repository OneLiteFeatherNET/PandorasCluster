package net.onelitefeather.pandorascluster.notification

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.onelitefeather.pandorascluster.api.EntityCategory
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.util.getEntityCount
import net.onelitefeather.pandorascluster.util.getEntityLimit

class MinecraftStaffNotification(private val pandorasClusterApi: PandorasClusterApi) : StaffNotification(pandorasClusterApi) {

    override fun notifyEntitySpawnLimit(land: Land, entityCategory: EntityCategory) {

        if (!canBeNotified(entityCategory)) return

        val players = pandorasClusterApi.getPlugin().server.onlinePlayers
        val landOwner = land.owner ?: return

        players.filter { it.hasPermission(getPermission()) }.forEach { player ->

            val ownerName = Component.text(landOwner.name!!)
            val entityCategoryName = Component.text(entityCategory.name.lowercase().replaceFirstChar { it.uppercase() })
            val hoverMessage = Component.translatable("staff.notification.mob.limit.reached.hover").arguments(
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
        return MiniMessage.miniMessage().deserialize(
            if (count >= limit) "<red><count></red>" else "<green><count></green>",
            Placeholder.unparsed("count", count.toString())
        )
    }

    private fun getPermission() = "pandorascluster.staff.notify"
}