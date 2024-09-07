package net.onelitefeather.pandorascluster.api.utils

import net.kyori.adventure.util.Services
import java.util.Optional
import kotlin.jvm.optionals.getOrDefault

interface LocationUtil {

    fun locToBlock(input: Double): Int

    object Instances {

        private val service: Optional<LocationUtil> = Services.service(LocationUtil::class.java)
        val instance = service.getOrDefault(DefaultLocationImpl())

    }

}