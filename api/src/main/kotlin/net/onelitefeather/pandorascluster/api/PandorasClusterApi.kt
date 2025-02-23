package net.onelitefeather.pandorascluster.api

import net.onelitefeather.pandorascluster.api.service.*

interface PandorasClusterApi {

    fun getDatabaseStorageService(): LandService

    fun getLandPlayerService(): LandPlayerService

    fun getDatabaseService(): DatabaseService

    fun getLandService(): LandService

    fun getLandFlagService(): LandFlagService

    fun getStaffNotification(): StaffNotificationService
}