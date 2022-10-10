package net.onelitefeather.pandorascluster.enums

import org.bukkit.permissions.Permissible

enum class Permission(val permissionNode: String) {


    ACCESS("pandorascluster.access.other"),
    BLOCK_BREAK("pandorascluster.bypass.block.break"),
    BLOCK_PLACE("pandorascluster.bypass.block.place"),
    ENTITY_MOUNT("pandorascluster.bypass.mount"),
    LAND_ENTRY_DENIED("pandorascluster.bypass.entry.denied"),

    PVP("pandorascluster.bypass.attack.player"),
    PVE("pandorascluster.bypass.attack.entity"),

    VEHICLE_ENTER("pandorascluster.bypass.vehicle.use"),
    VEHICLE_DAMAGE("pandorascluster.bypass.vehicle.damage"),
    VEHICLE_DESTROY("pandorascluster.bypass.vehicle.destroy"),
    EXPLOSION("pandorascluster.bypass.explosion"),
    INTERACT_CONTAINERS("pandorascluster.bypass.interact.container"),
    INTERACT_FARMLAND("pandorascluster.bypass.interact.farmland"),

    INTERACT_USE("pandorascluster.bypass.interact.use"),

    POTION_SPLASH("pandorascluster.bypass.potion.splash"),
    BUCKET_USE("pandorascluster.bypass.use.bucket"),
    SHEAR_ENTITY("pandorascluster.bypass.shear.entity"),
    SHEAR_BLOCK("pandorascluster.bypass.shear.block"),
    ENTER_BED("pandorascluster.bypass.bed.enter"),
    LEAVE_BED("pandorascluster.bypass.bed.leave"),
    TAKE_LECTERN("pandorascluster.bypass.lectern.take"),
    LEASH_ENTITY("pandorascluster.bypass.entity.leash"),
    UNLEASH_ENTITY("pandorascluster.bypass.entity.unleash"),
    TRIGGER_RAID("pandorascluster.bypass.raid.trigger"),

    HANGING_BREAK("pandorascluster.bypass.hanging.break"),
    HANGING_PLACE("pandorascluster.bypass.hanging.place"),
    TAME_ENTITY("pandorascluster.bypass.entity.tame"),
    PROJECTILE_HIT_ENTITY("pandorascluster.bypass.projectile.hit"),
    ENTITY_INTERACT("pandorascluster.bypass.interact.entity"),

    SET_LAND_ROLE("pandorascluster.admin.set.role");

    fun hasPermission(permissible: Permissible): Boolean {
        return permissible.hasPermission("pandorascluster.bypass.*") || permissible.hasPermission(permissionNode)
    }

}