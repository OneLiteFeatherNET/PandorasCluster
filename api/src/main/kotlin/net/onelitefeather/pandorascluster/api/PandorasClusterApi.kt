package net.onelitefeather.pandorascluster.api

import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.api.player.LandPlayer
import net.onelitefeather.pandorascluster.api.player.Player
import net.onelitefeather.pandorascluster.api.service.*
import java.util.*
import java.util.logging.Logger

interface PandorasClusterApi {

    fun pluginPrefix(): Component

    fun getDatabaseStorageService(): LandService

    fun getLandPlayerService(): LandPlayerService

    fun getDatabaseService(): DatabaseService

    fun getLandService(): LandService

    fun getLandFlagService(): LandFlagService

    fun getStaffNotification(): StaffNotificationService
}