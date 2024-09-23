package net.onelitefeather.pandorascluster.database.service

import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.api.land.flag.FlagRoleAttachment
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.api.service.DatabaseService
import net.onelitefeather.pandorascluster.api.service.LandFlagService
import net.onelitefeather.pandorascluster.api.utils.LOGGER
import net.onelitefeather.pandorascluster.database.models.flag.FlagRoleAttachmentEntity
import org.hibernate.HibernateException
import org.hibernate.Transaction

class DatabaseLandFlagService(private var databaseService: DatabaseService) : LandFlagService {

    override fun addLandFlag(landFlag: LandFlag, value: String, role: LandRole?, land: Land) {
        var transaction: Transaction? = null
        try {
            databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()

                val flag = FlagRoleAttachmentEntity(
                    null,
                    role ?: LandRole.MEMBER,
                    value,
                    landFlag,
                    databaseService.landMapper().modelToEntity(land)
                )

                session.persist(flag)
                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            LOGGER.throwing(this::class.java.simpleName, "addLandFlag", e)
        }
    }

    override fun updateLandFlag(flag: FlagRoleAttachment) {

        var transaction: Transaction? = null
        try {
            databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.merge(databaseService.flagMapper().modelToEntity(flag))
                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            LOGGER.throwing(this::class.java.simpleName, "updateLandFlag", e)
        }
    }

    override fun removeLandFlag(landFlagProperty: FlagRoleAttachment) {
        var transaction: Transaction? = null
        try {
            databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.remove(databaseService.flagMapper().modelToEntity(landFlagProperty))
                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            LOGGER.throwing(this::class.java.simpleName, "updateLandFlag", e)
        }
    }
}