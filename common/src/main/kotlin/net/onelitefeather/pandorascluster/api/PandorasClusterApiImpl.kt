package net.onelitefeather.pandorascluster.api

import net.onelitefeather.pandorascluster.api.service.*
import net.onelitefeather.pandorascluster.database.service.DatabaseLandFlagService
import net.onelitefeather.pandorascluster.database.service.DatabaseLandPlayerService
import net.onelitefeather.pandorascluster.database.service.DatabaseLandService
import net.onelitefeather.pandorascluster.database.service.DatabaseServiceImpl

class PandorasClusterApiImpl : PandorasClusterApi {

    private var databaseService: DatabaseService = DatabaseServiceImpl()
    private lateinit var landPlayerService: LandPlayerService
    private lateinit var landFlagService: LandFlagService
    private lateinit var landService: LandService
    private lateinit var staffNotification: StaffNotificationService

    init {
        databaseService.connect("connection.cfg.xml")
        if (databaseService.isRunning()) {
            landService = DatabaseLandService(this, databaseService)
            landFlagService = DatabaseLandFlagService(databaseService, landService)
            landPlayerService = DatabaseLandPlayerService(databaseService, landService)
            staffNotification = StaffNotificationService(this)
        }
    }

    override fun getDatabaseStorageService(): LandService = landService

    override fun getLandPlayerService(): LandPlayerService = landPlayerService

    override fun getDatabaseService(): DatabaseService = databaseService

    override fun getLandService(): LandService = landService

    override fun getLandFlagService(): LandFlagService = landFlagService

    override fun getStaffNotification(): StaffNotificationService = staffNotification


}