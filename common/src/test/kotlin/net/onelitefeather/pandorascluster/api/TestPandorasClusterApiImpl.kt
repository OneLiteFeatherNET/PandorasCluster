package net.onelitefeather.pandorascluster.api

import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.service.*
import net.onelitefeather.pandorascluster.database.service.DatabaseLandFlagService
import net.onelitefeather.pandorascluster.database.service.DatabaseLandPlayerService
import net.onelitefeather.pandorascluster.database.service.DatabaseLandService

class TestPandorasClusterApiImpl : PandorasClusterApi {

    private var databaseService: DatabaseService = TestDatabaseServiceImpl()
    private lateinit var landPlayerService: LandPlayerService
    private lateinit var landFlagService: LandFlagService
    private lateinit var landService: LandService
    private lateinit var staffNotification: StaffNotificationService

    init {
        databaseService.connect("connection.cfg.xml")
        if (databaseService.isRunning()) {
            landService = DatabaseLandService(this, databaseService)
            landFlagService = DatabaseLandFlagService(databaseService, landService)
            landPlayerService = DatabaseLandPlayerService(databaseService)
            staffNotification = StaffNotificationService(this)
        }
    }

    override fun pluginPrefix(): Component = Component.translatable("prefix")

    override fun getDatabaseStorageService(): LandService = landService

    override fun getLandPlayerService(): LandPlayerService = landPlayerService

    override fun getDatabaseService(): DatabaseService = databaseService

    override fun getLandService(): LandService = landService

    override fun getLandFlagService(): LandFlagService = landFlagService

    override fun getStaffNotification(): StaffNotificationService = staffNotification


}