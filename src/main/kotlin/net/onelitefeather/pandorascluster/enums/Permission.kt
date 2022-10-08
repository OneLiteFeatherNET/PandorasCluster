package net.onelitefeather.pandorascluster.enums

import org.bukkit.permissions.Permissible

enum class Permission(val permissionNode: String) {

    SET_LAND_ROLE("pandorascluster.admin.set.role"),
    UNOWNED_CHUNK("pandorascluster.unowned.access"),
    BLOCK_BREAK("pandorascluster.unowned.block.break"),
    BLOCK_PLACE("pandorascluster.unowned.block.place"),
    OWNED_CHUNK("pandorascluster.owned.access"),
    LAND_ENTRY_DENIED("pandorascluster.owned.entry.denied"),
    INTERACT_CONTAINERS("pandorascluster.owned.interact.container"),

    @Deprecated(message = "This field will be removed in future releases")
    ENTITY_MOUNT("pandorascluster.bypass.mount"),

    @Deprecated(message = "This field will be removed in future releases")
    PVP("pandorascluster.bypass.attack.player"),

    @Deprecated(message = "This field will be removed in future releases")
    PVE("pandorascluster.bypass.attack.entity"),

    @Deprecated(message = "This field will be removed in future releases")
    VEHICLE_ENTER("pandorascluster.bypass.vehicle.use"),

    @Deprecated(message = "This field will be removed in future releases")
    VEHICLE_DAMAGE("pandorascluster.bypass.vehicle.damage"),

    @Deprecated(message = "This field will be removed in future releases")
    VEHICLE_DESTROY("pandorascluster.bypass.vehicle.destroy"),

    @Deprecated(message = "This field will be removed in future releases")
    EXPLOSION("pandorascluster.bypass.explosion"),

    @Deprecated(message = "This field will be removed in future releases")
    INTERACT_CROPS("pandorascluster.bypass.interact.crops"),

    @Deprecated(message = "This field will be removed in future releases")
    INTERACT_USE("pandorascluster.bypass.interact.use"),

    @Deprecated(message = "This field will be removed in future releases")
    BUCKET_USE("pandorascluster.bypass.use.bucket"),

    @Deprecated(message = "This field will be removed in future releases")
    ENTER_BED("pandorascluster.bypass.bed.enter"),

    @Deprecated(message = "This field will be removed in future releases")
    LEAVE_BED("pandorascluster.bypass.bed.leave"),

    @Deprecated(message = "This field will be removed in future releases")
    TAME_ENTITY("pandorascluster.bypass.entity.tame"),

    @Deprecated(message = "This field will be removed in future releases")
    POTION_SPLASH("pandorascluster.bypass.potion.splash"),

    @Deprecated(message = "This field will be removed in future releases")
    SHEAR_ENTITY("pandorascluster.bypass.shear.entity"),

    @Deprecated(message = "This field will be removed in future releases")
    SHEAR_BLOCK("pandorascluster.bypass.shear.block"),

    @Deprecated(message = "This field will be removed in future releases")
    TAKE_LECTERN("pandorascluster.bypass.lectern.take"),

    @Deprecated(message = "This field will be removed in future releases")
    LEASH_ENTITY("pandorascluster.bypass.entity.leash"),

    @Deprecated(message = "This field will be removed in future releases")
    UNLEASH_ENTITY("pandorascluster.bypass.entity.unleash"),

    @Deprecated(message = "This field will be removed in future releases")
    TRIGGER_RAID("pandorascluster.bypass.raid.trigger"),

    @Deprecated(message = "This field will be removed in future releases")
    HANGING_BREAK("pandorascluster.bypass.hanging.break"),

    @Deprecated(message = "This field will be removed in future releases")
    HANGING_PLACE("pandorascluster.bypass.hanging.place"),

    @Deprecated(message = "This field will be removed in future releases")
    PROJECTILE_HIT_ENTITY("pandorascluster.bypass.projectile.hit"),

    SET_LAND_ROLE("pandorascluster.admin.set.role"),
    REMOVE_PLAYER_OTHER_LAND("pandorascluster.land.remove.others"),
    SET_LAND_FLAG("pandorascluster.admin.set.flags"),
    SET_LAND_HOME("pandorascluster.admin.set.home"),
    SET_LAND_OWNER("pandorascluster.admin.set.owner");

    fun hasPermission(permissible: Permissible): Boolean {
        return permissible.hasPermission("pandorascluster.bypass.*") || permissible.hasPermission(permissionNode)
    }
}