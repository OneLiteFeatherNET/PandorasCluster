package net.onelitefeather.pandorascluster.extensions

import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.util.IGNORE_CLAIM_LIMIT
import org.apache.commons.lang3.StringUtils
import org.bukkit.entity.AnimalTamer
import org.bukkit.entity.Entity
import org.bukkit.entity.Tameable
import org.bukkit.permissions.Permissible

interface EntityUtils {

    fun isPetOwner(tameable: Tameable, animalTamer: AnimalTamer): Boolean = tameable.owner?.uniqueId == animalTamer.uniqueId

    fun canMountEntity(mount: Entity, entity: Entity, land: Land): Boolean {

        if (land.hasAccess(entity.uniqueId)) return true
        if (land.getLandFlag(LandFlag.ENTITY_MOUNT).getValue<Boolean>() == true) return true

        if (mount is Tameable && entity is AnimalTamer) {
            return mount.isTamed && isPetOwner(mount, entity)
        }

        return hasPermission(entity, LandFlag.ENTITY_MOUNT)
    }

    fun getHighestClaimLimit(permissible: Permissible): Int {
        if(hasPermission(permissible, Permission.NO_CLAIM_LIMIT)) return IGNORE_CLAIM_LIMIT
        val permissionAttach =
            permissible.effectivePermissions.filter { it.permission.startsWith(Permission.CLAIM_LIMIT.permissionNode) }.maxByOrNull{
                val last = it.permission.substringAfterLast(".")
                val limit = if (StringUtils.isNumeric(last)) last.toInt() else 0
                limit
            } ?: return 0

        val amount = permissionAttach.permission.substringAfterLast(".")
        return if(StringUtils.isNumeric(amount)) amount.toInt() else 0
    }

    fun hasPermission(permissible: Permissible, landFlag: LandFlag) = permissible.hasPermission(Permission.FLAG_PERMISSION.permissionNode.format(landFlag.name))

    fun hasPermission(permissible: Permissible, permission: Permission) = permissible.hasPermission(permission.permissionNode)

    fun hasRangedPermission(permissible: Permissible, basePermission: String, range: Int): Boolean {
        val permissionAttach = permissible.effectivePermissions.filter { it.permission.startsWith(basePermission) }.maxByOrNull {
            val last = it.permission.substringAfterLast(".")
            val limit = if (StringUtils.isNumeric(last)) last.toInt() else 0
            limit >= range
        } ?: return false

        val amount = permissionAttach.permission.substringAfterLast(".")
        return StringUtils.isNumeric(amount) && amount.toInt() >= range
    }
}



