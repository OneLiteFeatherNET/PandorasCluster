package net.onelitefeather.pandorascluster.api.utils

import jdk.jshell.spi.ExecutionControl.NotImplementedException
import java.util.*

class DefaultPlayerImpl : PlayerUtil {
    override fun getPlayer(uuid: UUID): Any {
        throw NotImplementedException("This is not implemented yet!")
    }

    override fun isOnline(uuid: UUID): Boolean {
        throw NotImplementedException("This is not implemented yet!")
    }


}