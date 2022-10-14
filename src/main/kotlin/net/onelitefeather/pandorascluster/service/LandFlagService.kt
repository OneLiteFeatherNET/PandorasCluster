package net.onelitefeather.pandorascluster.service

import io.sentry.Sentry
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LAND_FLAGS
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.util.DUMMY_LAND
import org.hibernate.HibernateException
import java.util.logging.Level

class LandFlagService(private val pandorasClusterApi: PandorasClusterApi) {

    private val defaultFlags = arrayListOf<LandFlagEntity>()

    init {
        LAND_FLAGS.forEach {
            if(it == LandFlag.UNKNOWN) return@forEach
            defaultFlags.add(
                LandFlagEntity(
                    null,
                    it.name,
                    it.defaultValue.toString(),
                    it.type,
                    it.landFlagType,
                    DUMMY_LAND
                )
            )
        }
    }

    fun getDefaultFlags(): List<LandFlagEntity> {
        return defaultFlags
    }

    fun getDefaultFlag(landFlag: LandFlag): LandFlagEntity {
        return getDefaultFlags().first { it.name == landFlag.name }
    }


    fun existsFlagInLand(landFlag: LandFlag?, land: Land): Boolean {
        return getLandFlag(landFlag ?: LandFlag.UNKNOWN, land) != null
    }

    fun getLandFlag(landFlag: LandFlag, land: Land): LandFlagEntity? {

        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val flagOfLand = session.createQuery(
                    "SELECT f FROM LandFlagEntity f JOIN FETCH f.land l WHERE l.id = :landId AND f.name = :name",
                    LandFlagEntity::class.java
                )
                flagOfLand.setParameter("landId", land.id)
                flagOfLand.setParameter("name", landFlag.name)
                return flagOfLand.uniqueResult()
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot load landflag $landFlag", e)
            Sentry.captureException(e)
        }

        return getDefaultFlag(landFlag)
    }

    fun getFlagsByLand(land: Land): List<LandFlagEntity> {
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                val flagsOfLand = session.createQuery(
                    "SELECT f FROM LandFlagEntity f JOIN FETCH f.land l WHERE l.id = :landId",
                    LandFlagEntity::class.java
                )
                flagsOfLand.setParameter("landId", land.id)
                return flagsOfLand.list()
            }
        } catch (e: HibernateException) {
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot load flags by land", e)
            Sentry.captureException(e)
        }

        return emptyList()
    }
}