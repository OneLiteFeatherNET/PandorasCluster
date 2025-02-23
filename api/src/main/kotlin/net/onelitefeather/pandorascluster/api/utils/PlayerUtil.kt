package net.onelitefeather.pandorascluster.api.utils

import net.kyori.adventure.util.Services
import net.onelitefeather.pandorascluster.api.enums.Permission
import java.util.*
import kotlin.jvm.optionals.getOrDefault

interface PlayerUtil {

    fun getPlayer(uuid: UUID): Any?

    fun isOnline(uuid: UUID): Boolean

    fun hasPermission(uuid: UUID, permissionNode: String): Boolean

    fun hasPermission(uuid: UUID, permission: Permission) = hasPermission(uuid, permission.permissionNode)

    object Instances {

        private val service: Optional<PlayerUtil> = Services.service(PlayerUtil::class.java)
        val instance = service.getOrDefault(DefaultPlayerImpl())

    }
}