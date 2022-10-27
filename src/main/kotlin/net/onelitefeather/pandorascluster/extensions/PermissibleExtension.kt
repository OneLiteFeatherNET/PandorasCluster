package net.onelitefeather.pandorascluster.extensions

import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.util.IGNORE_CLAIM_LIMIT
import org.apache.commons.lang3.StringUtils
import org.bukkit.permissions.Permissible

fun Permissible.getHighestClaimLimit(): Int {
    if(this.hasPermission(Permission.NO_CLAIM_LIMIT)) return IGNORE_CLAIM_LIMIT
    val permissionAttach =
        this.effectivePermissions.filter { it.permission.startsWith(Permission.CLAIM_LIMIT.permissionNode) }.maxByOrNull{
            val last = it.permission.substringAfterLast(".")
            val limit = if (StringUtils.isNumeric(last)) last.toInt() else 0
            limit
        } ?: return 0

    val amount = permissionAttach.permission.substringAfterLast(".")
    return if(StringUtils.isNumeric(amount)) amount.toInt() else 0
}

fun Permissible.hasPermission(landFlag: LandFlag) = this.hasPermission(Permission.FLAG_PERMISSION.permissionNode.format(landFlag.name))

fun Permissible.hasPermission(permission: Permission) = this.hasPermission(permission.permissionNode)

fun Permissible.hasRangedPermission(basePermission: String, range: Int): Boolean {
    val permissionAttach = this.effectivePermissions.filter { it.permission.startsWith(basePermission) }.maxByOrNull {
        val last = it.permission.substringAfterLast(".")
        val limit = if (StringUtils.isNumeric(last)) last.toInt() else 0
        limit >= range
    } ?: return false

    val amount = permissionAttach.permission.substringAfterLast(".")
    return StringUtils.isNumeric(amount) && amount.toInt() >= range
}