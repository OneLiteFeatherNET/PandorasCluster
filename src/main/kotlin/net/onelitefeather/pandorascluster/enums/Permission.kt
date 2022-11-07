package net.onelitefeather.pandorascluster.enums

import org.bukkit.permissions.Permissible
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval

@Suppress("kotlin:S1133")
enum class Permission(val permissionNode: String) {

    NO_CLAIM_LIMIT("pandorascluster.unlimit.claim"),
    FLAG_PERMISSION("pandorascluster.flags.flag.%s"),
    CLAIM_LIMIT("pandorascluster.limit.claim"),
    SET_LAND_ROLE("pandorascluster.admin.set.role"),
    SET_LAND_FLAG("pandorascluster.admin.set.flags"),
    SET_LAND_HOME("pandorascluster.admin.set.home"),
    SET_LAND_OWNER("pandorascluster.admin.set.owner"),
    UNOWNED_CHUNK("pandorascluster.unowned.access"),
    BLOCK_BREAK("pandorascluster.unowned.block.break"),
    BLOCK_PLACE("pandorascluster.unowned.block.place"),
    OWNED_CHUNK("pandorascluster.owned.access"),
    LAND_ENTRY_DENIED("pandorascluster.owned.entry.denied"),
    INTERACT_CONTAINERS("pandorascluster.owned.interact.container"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    ENTITY_MOUNT("pandorascluster.bypass.mount"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    PVP("pandorascluster.bypass.attack.player"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    PVE("pandorascluster.bypass.attack.entity"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    VEHICLE_ENTER("pandorascluster.bypass.vehicle.use"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    VEHICLE_DAMAGE("pandorascluster.bypass.vehicle.damage"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    VEHICLE_DESTROY("pandorascluster.bypass.vehicle.destroy"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    EXPLOSION("pandorascluster.bypass.explosion"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    INTERACT_CROPS("pandorascluster.bypass.interact.crops"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    INTERACT_USE("pandorascluster.bypass.interact.use"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    BUCKET_USE("pandorascluster.bypass.use.bucket"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    ENTER_BED("pandorascluster.bypass.bed.enter"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    LEAVE_BED("pandorascluster.bypass.bed.leave"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    TAME_ENTITY("pandorascluster.bypass.entity.tame"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    POTION_SPLASH("pandorascluster.bypass.potion.splash"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    SHEAR_ENTITY("pandorascluster.bypass.shear.entity"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    SHEAR_BLOCK("pandorascluster.bypass.shear.block"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    TAKE_LECTERN("pandorascluster.bypass.lectern.take"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    LEASH_ENTITY("pandorascluster.bypass.entity.leash"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    UNLEASH_ENTITY("pandorascluster.bypass.entity.unleash"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    TRIGGER_RAID("pandorascluster.bypass.raid.trigger"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    HANGING_BREAK("pandorascluster.bypass.hanging.break"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    HANGING_PLACE("pandorascluster.bypass.hanging.place"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    PROJECTILE_HIT_ENTITY("pandorascluster.bypass.projectile.hit"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    ENTITY_INTERACT("pandorascluster.bypass.interact.entity"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    FERTILIZE_BLOCK("pandorascluster.bypass.interact.fertilize"),

    @Deprecated(message = "This field will be removed in future releases")
    @ScheduledForRemoval(inVersion = "1.2.0")
    ENTITY_CHANGE_BLOCK("pandorascluster.bypass.block.change");

    fun hasPermission(permissible: Permissible): Boolean {
        return permissible.hasPermission("pandorascluster.bypass.*") || permissible.hasPermission(permissionNode)
    }
}