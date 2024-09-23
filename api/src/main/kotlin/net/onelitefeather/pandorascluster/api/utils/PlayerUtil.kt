package net.onelitefeather.pandorascluster.api.utils

import net.kyori.adventure.util.Services
import java.util.*
import kotlin.jvm.optionals.getOrDefault

interface PlayerUtil {

    fun getPlayer(uuid: UUID): Any?

    fun isOnline(uuid: UUID): Boolean

    object Instances {

        private val service: Optional<PlayerUtil> = Services.service(PlayerUtil::class.java)
        val instance = service.getOrDefault(DefaultPlayerImpl())

    }
}