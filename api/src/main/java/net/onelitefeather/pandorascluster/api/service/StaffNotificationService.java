package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.enums.EntityCategory;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.notification.StaffNotification;

import java.util.ArrayList;
import java.util.List;

public class StaffNotificationService {

    private final List<StaffNotification> staffNotifications = new ArrayList<>();

    /**
     * Register a new [StaffNotification]
     * @param staffNotification the staff notification to be added.
     */

    public void addStaffNotification(StaffNotification staffNotification) {
        this.staffNotifications.add(staffNotification);
    }

    /**
     * @param land the suspicious land
     * @param category the entity category
     */
    public void notify(Land land, EntityCategory category) {
        staffNotifications.forEach(staffNotification -> {
            staffNotification.notifyEntitySpawnLimit(land, category);
        });
    }
}
