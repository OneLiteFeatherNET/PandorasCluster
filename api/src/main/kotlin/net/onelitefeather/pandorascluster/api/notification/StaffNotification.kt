package net.onelitefeather.pandorascluster.api.notification

import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.enums.EntityCategory
import java.util.*

abstract class StaffNotification(private val pandorasClusterApi: PandorasClusterApi) {

    private val staffNotificationCooldown: MutableMap<EntityCategory, Long> = EnumMap(EntityCategory::class.java)

    /**
     * @param entityCategory the entity category
     * @return true if the cooldown is over
     */
    fun canBeNotified(entityCategory: EntityCategory) =
        System.currentTimeMillis() >= staffNotificationCooldown.getOrDefault(entityCategory, 0L)

    /**
     * @param entityCategory the entity category
     * @param cooldown the cooldown time in minutes
     */
    fun updateCooldown(entityCategory: EntityCategory, cooldown: Int?) {
        val time = cooldown ?: 1
        staffNotificationCooldown[entityCategory] = System.currentTimeMillis() + 1000 * 60 * time
    }

    /**
     * @param land the land
     * @param entityCategory the entity category
     */
    abstract fun notifyEntitySpawnLimit(land: Land, entityCategory: EntityCategory)

}