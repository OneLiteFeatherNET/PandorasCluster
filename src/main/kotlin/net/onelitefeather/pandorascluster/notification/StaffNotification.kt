package net.onelitefeather.pandorascluster.notification

import net.onelitefeather.pandorascluster.api.EntityCategory
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.Land
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
     * @param cooldown the cooldown time
     */
    fun updateCooldown(entityCategory: EntityCategory, cooldown: Long) {
        staffNotificationCooldown[entityCategory] = cooldown
    }

    protected fun cooldown() =
        System.currentTimeMillis() + 1000 * 60 * pandorasClusterApi.getPlugin().config.getInt("staff.notification.cooldown")


    /**
     * @param land the land
     * @param entityCategory the entity category
     */
    abstract fun notifyEntitySpawnLimit(land: Land, entityCategory: EntityCategory)

}