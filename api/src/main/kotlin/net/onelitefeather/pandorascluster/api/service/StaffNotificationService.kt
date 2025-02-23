package net.onelitefeather.pandorascluster.api.service

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.enums.EntityCategory
import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.notification.StaffNotification

class StaffNotificationService(private val pandorasClusterApi: PandorasClusterApi) {

    private val staffNotifications: MutableList<StaffNotification> = arrayListOf()

    /**
     * Register a new [StaffNotification]
     * @param staffNotification
     */
    fun addStaffNotification(staffNotification: StaffNotification) {
        staffNotifications.add(staffNotification)
    }

    /**
     * @param land the suspicious land
     * @param category the entity category
     */
    fun notify(land: Land, category: EntityCategory) {
        staffNotifications.forEach { it.notifyEntitySpawnLimit(land, category) }
    }
}