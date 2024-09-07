package net.onelitefeather.pandorascluster.api.utils

import net.kyori.adventure.util.Services
import net.onelitefeather.pandorascluster.api.models.Player
import java.util.*
import kotlin.jvm.optionals.getOrDefault

interface PlayerUtil {

    fun getPlayer(uuid: UUID): Player

    object Instances {

        private val service: Optional<PlayerUtil> = Services.service(PlayerUtil::class.java)
        val instance = service.getOrDefault(DefaultPlayerImpl())

    }
}