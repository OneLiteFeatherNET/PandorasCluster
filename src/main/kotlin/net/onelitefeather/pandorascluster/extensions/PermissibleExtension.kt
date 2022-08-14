package net.onelitefeather.pandorascluster.extensions

import net.onelitefeather.pandorascluster.enums.Permission
import org.bukkit.permissions.Permissible

fun Permissible.hasPermission(permission: Permission) = this.hasPermission(permission.permissionNode)
