package net.onelitefeather.pandorascluster.enums

import org.bukkit.permissions.Permissible

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
    INTERACT_FARMLAND("pandorascluster.unowned.interact.farmland");

    fun hasPermission(permissible: Permissible): Boolean {
        return permissible.hasPermission(permissionNode)
    }
}