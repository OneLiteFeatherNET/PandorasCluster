package net.onelitefeather.pandorascluster.database.service

import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.land.flag.FlagRoleAttachment
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.api.service.DatabaseService
import net.onelitefeather.pandorascluster.api.service.LandFlagService
import net.onelitefeather.pandorascluster.api.service.LandService
import net.onelitefeather.pandorascluster.api.utils.LOGGER
import net.onelitefeather.pandorascluster.database.models.LandEntity
import net.onelitefeather.pandorascluster.database.models.flag.FlagRoleAttachmentEntity
import org.hibernate.HibernateException
import org.hibernate.Transaction

class DatabaseLandFlagService(private var databaseService: DatabaseService,
                              private val databaseLandService: LandService) : LandFlagService {

    override fun addLandFlag(landFlag: LandFlag, role: LandRole?, land: Land) {
        if (land.hasFlag(landFlag)) return
        var transaction: Transaction? = null
        try {
            databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()

                val flag = FlagRoleAttachmentEntity(
                    null,
                    role ?: LandRole.MEMBER,
                    landFlag,
                    databaseService.landMapper().modelToEntity(land) as LandEntity?
                )

                session.persist(flag)
                transaction?.commit()
            }

            databaseLandService.updateLoadedChunks(land)
        } catch (e: HibernateException) {
            transaction?.rollback()
            LOGGER.throwing(this::class.java.simpleName, "addLandFlag", e)
        }
    }

    override fun updateLandFlag(flag: FlagRoleAttachment, land: Land) {

        var transaction: Transaction? = null
        try {
            databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()

                val landEntity = databaseService.landMapper().modelToEntity(land) as LandEntity
                val flagEntity = (databaseService.flagMapper().modelToEntity(flag) as FlagRoleAttachmentEntity).copy(
                    land = landEntity
                )

                session.merge(flagEntity)
                transaction?.commit()
            }

            databaseLandService.updateLoadedChunks(land)
        } catch (e: HibernateException) {
            transaction?.rollback()
            LOGGER.throwing(this::class.java.simpleName, "updateLandFlag", e)
        }
    }

    override fun removeLandFlag(flag: FlagRoleAttachment, land: Land) {
        var transaction: Transaction? = null
        try {
            databaseService.sessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()

                val landEntity = databaseService.landMapper().modelToEntity(land) as LandEntity
                val flagEntity = (databaseService.flagMapper().modelToEntity(flag) as FlagRoleAttachmentEntity).copy(
                    land = landEntity
                )

                session.remove(flagEntity)
                transaction?.commit()
            }

            databaseLandService.updateLoadedChunks(land)
        } catch (e: HibernateException) {
            transaction?.rollback()
            LOGGER.throwing(this::class.java.simpleName, "updateLandFlag", e)
        }
    }

    override fun getLandFlag(landFlag: LandFlag, land: Land): FlagRoleAttachment? {
        try {
            databaseService.sessionFactory().openSession().use { session ->
                val flagOfLand = session.createQuery(
                    "SELECT f FROM FlagRoleAttachmentEntity f JOIN FETCH f.land l WHERE l.id = :landId AND f.landFlag = :landFlag",
                    FlagRoleAttachmentEntity::class.java
                )
                flagOfLand.setParameter("landId", land.id)
                flagOfLand.setParameter("landFlag", landFlag)
                return databaseService.flagMapper().entityToModel(flagOfLand.uniqueResult())
            }
        } catch (e: HibernateException) {
            LOGGER.throwing(this::class.java.simpleName, "getLandFlag", e)
        }
        return null

    }

    override fun getFlags(land: Land): List<FlagRoleAttachment> {
        try {
            databaseService.sessionFactory().openSession().use { session ->
                val flagsOfLand = session.createQuery(
                    "SELECT f FROM FlagRoleAttachmentEntity f JOIN FETCH f.land l WHERE l.id = :landId",
                    FlagRoleAttachmentEntity::class.java
                )
                flagsOfLand.setParameter("landId", land.id)
                return flagsOfLand.list().mapNotNull { databaseService.flagMapper().entityToModel(it) }
            }
        } catch (e: HibernateException) {
            LOGGER.throwing(this::class.java.simpleName, "getFlags", e)
        }
        return emptyList()
    }

}