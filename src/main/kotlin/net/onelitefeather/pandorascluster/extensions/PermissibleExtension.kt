package net.onelitefeather.pandorascluster.extensions

import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import org.bukkit.permissions.Permissible

private const val basePermission = "pandorascluster.flags.flag.%s"

fun Permissible.hasPermission(landFlag: LandFlag) = this.hasPermission(basePermission.format(landFlag.name))

fun Permissible.hasPermission(permission: Permission) = this.hasPermission(permission.permissionNode)