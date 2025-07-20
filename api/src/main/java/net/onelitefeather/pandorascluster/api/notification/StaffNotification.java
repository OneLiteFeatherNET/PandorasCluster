package net.onelitefeather.pandorascluster.api.notification;

import net.onelitefeather.pandorascluster.api.enums.EntityCategory;
import net.onelitefeather.pandorascluster.api.land.Land;

import java.util.EnumMap;
import java.util.Map;

public abstract class StaffNotification {

    private static final Map<EntityCategory, Long> staffNotificationCooldown = new EnumMap<>(EntityCategory.class);

    public boolean canBeNotified(EntityCategory entityCategory) {
        return System.currentTimeMillis() >= staffNotificationCooldown.getOrDefault(entityCategory, 0L);
    }

    /**
     * @param entityCategory the entity category
     * @param cooldown the cooldown time in minutes
     */
    public void updateCooldown(EntityCategory entityCategory, Integer cooldown) {
        var time = cooldown != null ? cooldown : 1;
        staffNotificationCooldown.put(entityCategory, System.currentTimeMillis() + 1000L * 60 * time);
    }

    /**
     * @param land the land
     * @param entityCategory the entity category
     */
    public abstract void notifyEntitySpawnLimit(Land land, EntityCategory entityCategory);
}