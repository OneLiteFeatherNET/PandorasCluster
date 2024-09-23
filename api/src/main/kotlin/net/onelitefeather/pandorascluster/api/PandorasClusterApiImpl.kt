package net.onelitefeather.pandorascluster.api

import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.service.*
import net.onelitefeather.pandorascluster.database.service.DatabaseLandFlagService
import net.onelitefeather.pandorascluster.database.service.DatabaseLandPlayerService
import net.onelitefeather.pandorascluster.database.service.DatabaseLandService
import net.onelitefeather.pandorascluster.database.service.DatabaseServiceImpl
import java.nio.file.Path

class PandorasClusterApiImpl : PandorasClusterApi {

    private var databaseService: DatabaseService = DatabaseServiceImpl()
    private lateinit var landPlayerService: LandPlayerService
    private lateinit var landFlagService: LandFlagService
    private lateinit var landService: LandService
    private lateinit var staffNotification: StaffNotificationService

    //pandorasClusterPlugin.dataFolder.toPath().resolve("hibernate.cfg.xml")

    init {
        databaseService.connect(Path.of(""))
        if (databaseService.isRunning()) {
            landFlagService = DatabaseLandFlagService(databaseService)
            landPlayerService = DatabaseLandPlayerService(databaseService)
            landService = DatabaseLandService(this, databaseService)
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