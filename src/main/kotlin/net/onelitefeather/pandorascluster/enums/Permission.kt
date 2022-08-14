package net.onelitefeather.pandorascluster.enums

import org.bukkit.permissions.Permissible

enum class Permission(val permissionNode: String) {

    BLOCK_BREAK("pandorascluster.unowned.block.break"),
    BLOCK_PLACE("pandorascluster.unowned.block.place"),
    ENTITY_MOUNT("pandorascluster.unowned.mount"),
    LAND_ENTRY_DENIED("pandorascluster.unowned.entry.denied"),

    PVP("pandorascluster.unowned.attack.player"),
    PVE("pandorascluster.unowned.attack.entity"),

    VEHICLE_ENTER("pandorascluster.unowned.vehicle.use"),
    VEHICLE_DAMAGE("pandorascluster.unowned.vehicle.damage"),
    VEHICLE_DESTROY("pandorascluster.unowned.vehicle.destroy"),
    EXPLOSION("pandorascluster.unowned.explosion"),
    INTERACT_CONTAINERS("pandorascluster.unowned.interact.container"),
    INTERACT_FARMLAND("pandorascluster.unowned.interact.farmland"),
    INTERACT_USE("pandorascluster.unowned.interact.use"),
    POTION_SPLASH("pandorascluster.unowned.potion.splash"),
    PROJECTILE_HIT_ENTITY("pandorascluster.unowned.projectile.hit");


    fun hasPermission(permissible: Permissible): Boolean {
        return permissible.hasPermission(permissionNode)
    }

}