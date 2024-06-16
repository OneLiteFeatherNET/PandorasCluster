package net.onelitefeather.pandorascluster.notification

import net.onelitefeather.pandorascluster.api.EntityCategory
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.Land
import java.util.*

abstract class StaffNotification(private val pandorasClusterApi: PandorasClusterApi) {

    private val staffNotificationCooldown: MutableMap<EntityCategory, Long> = EnumMap(EntityCategory::class.java)

    fun canBeNotified(entityCategory: EntityCategory) =
        System.currentTimeMillis() >= staffNotificationCooldown.getOrDefault(entityCategory, 0L)

    fun updateCooldown(entityCategory: EntityCategory, cooldown: Long) {
        staffNotificationCooldown[entityCategory] = cooldown
    }

    protected fun cooldown() =
        System.currentTimeMillis() + 1000 * 60 * pandorasClusterApi.getPlugin().config.getInt("staff.notification.cooldown")


    abstract fun notifyEntitySpawnLimit(land: Land, entityCategory: EntityCategory)

}