package net.onelitefeather.pandorascluster.api.utils

import net.kyori.adventure.util.Services
import net.onelitefeather.pandorascluster.api.models.dto.PandorasPlayer
import java.util.*
import kotlin.jvm.optionals.getOrDefault

interface PlayerUtil {

    fun getPlayer(uuid: UUID): PandorasPlayer

    object Instances {

        private val service: Optional<PlayerUtil> = Services.service(PlayerUtil::class.java)
        val instance = service.getOrDefault(DefaultPlayerImpl())

    }
}