package net.onelitefeather.pandorascluster.api.utils

import jdk.jshell.spi.ExecutionControl.NotImplementedException
import java.util.*

class DefaultPlayerImpl : PlayerUtil {
    override fun getPlayer(uuid: UUID): Any {
        throw NotImplementedException("This is not implemented yet!")
    }

    override fun isOnline(uuid: UUID) = false

    override fun hasPermission(uuid: UUID, permission: String) = false
}