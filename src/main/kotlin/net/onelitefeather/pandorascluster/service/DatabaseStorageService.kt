package net.onelitefeather.pandorascluster.service

import io.sentry.Sentry
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.builder.landBuilder
import net.onelitefeather.pandorascluster.enums.LandRole
import net.onelitefeather.pandorascluster.land.ChunkPlaceholder
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.land.flag.getDefaultFlag
import net.onelitefeather.pandorascluster.land.player.LandMember
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.land.position.HomePosition
import net.onelitefeather.pandorascluster.land.position.toHomePosition
import org.apache.commons.lang3.StringUtils
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.entity.Player
import org.hibernate.HibernateException
import org.hibernate.Transaction
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.logging.Level

class DatabaseStorageService(val pandorasClusterApi: PandorasClusterApi) {

    fun updateLandHome(homePosition: HomePosition, ownerId: UUID) {
        var transaction: Transaction? = null
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()

                val stored = pandorasClusterApi.getLandService().getHome(ownerId)
                if (stored != null) {

                    session.merge(
                        stored.copy(
                            id = stored.id,
                            posX = homePosition.posX,
                            posY = homePosition.posY,
                            posZ = homePosition.posZ,
                            yaw = homePosition.yaw,
                            pitch = homePosition.pitch
                        )
                    )

                } else {
                    session.persist(homePosition)
                }


                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.getLogger()
                .log(Level.SEVERE, String.format("Cannot update land home %s", homePosition), e)
            Sentry.captureException(e)
        }
    }

    fun addLandMember(land: Land, member: LandPlayer, landRole: LandRole?) {
        val role = landRole ?: LandRole.VISITOR
        val landMember = LandMember(null, member, role, land)
        var transaction: Transaction? = null
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()

                val storedMember = pandorasClusterApi.getLandService().getLandMember(land, member)
                if (storedMember != null) {
                    session.merge(storedMember.copy(role = role, member = member, land = land))
                } else {
                    session.persist(landMember)
                }

                transaction?.commit()
                pandorasClusterApi.getLandService().updateLoadedChunks(land)
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.getLogger().log(
                Level.SEVERE,
                "Cannot save land member $landMember",
                e
            )
            Sentry.captureException(e)
        }
    }

    fun setLandOwner(land: Land, landPlayer: LandPlayer) {
        updateLand(land.copy(owner = landPlayer))
    }

    fun addUseMaterial(land: Land, material: String) {

        val list = land.getUseMaterials()
        val builder = StringBuilder()

        if(list.isNotEmpty()) {
            val lastElement = list.last()
            for (currentMaterial in list) {
                if (currentMaterial != lastElement) {
                    builder.append(currentMaterial.name).append(",")
                } else {
                    builder.append(currentMaterial.name).append(",").append(material)
                }
            }

            updateLandFlag(LandFlag.USE, builder.toString(), land)
        }
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

    fun removeUseMaterial(land: Land, material: Material) {
        val lastElement = land.getUseMaterials().last()
        val currentValue = land.getLandFlag(LandFlag.USE).value
        val toRemove = if (lastElement != material) material.name + "," else material.name
        updateLandFlag(LandFlag.USE, StringUtils.remove(currentValue, toRemove), land)
    }

    fun updateLandFlag(landFlag: LandFlag, value: String, land: Land) {

        var transaction: Transaction? = null
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                if (!land.hasFlag(landFlag)) {
                    session.persist(
                        LandFlagEntity(
                            null,
                            landFlag.name,
                            value,
                            landFlag.type,
                            landFlag.landFlagType,
                            land
                        )
                    )
                } else {
                    session.merge(getLandFlag(landFlag, land)?.copy(value = value))
                }

                transaction?.commit()
                pandorasClusterApi.getLandService().updateLoadedChunks(land)
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.getLogger()
                .log(Level.SEVERE, String.format("Cannot update landflag %s", landFlag), e)
            Sentry.captureException(e)
        }
    }

    fun updateLand(land: Land) {
        var transaction: Transaction? = null
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.merge(land)
                transaction?.commit()
                pandorasClusterApi.getLandService().updateLoadedChunks(land)
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.getLogger().log(Level.SEVERE, String.format("Cannot update land %s", land), e)
            Sentry.captureException(e)
        }
    }

    fun addChunkPlaceholder(chunk: Chunk, land: Land?) {
        var transaction: Transaction? = null
        val chunkPlaceholder = ChunkPlaceholder(null, chunk.chunkKey, land)
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.persist(chunkPlaceholder)
                transaction?.commit()
                if (land != null) {
                    pandorasClusterApi.getLandService().claimChunk(chunk)
                }
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.getLogger().log(
                Level.SEVERE,
                "Cannot save chunk placeholder $chunkPlaceholder",
                e
            )
            Sentry.captureException(e)
        }
    }

    fun createLand(owner: LandPlayer, player: Player, chunk: Chunk) {
        if (pandorasClusterApi.hasPlayerLand(player.uniqueId)) return

        CompletableFuture.runAsync {
            var transaction: Transaction? = null
            try {
                pandorasClusterApi.getSessionFactory().openSession().use { session ->
                    transaction = session.beginTransaction()

                    val homePosition = toHomePosition(player.location)
                    session.persist(homePosition)

                    val land = landBuilder {
                        chunkX { chunk.x }
                        chunkZ { chunk.z }
                        owner { owner }
                        homePosition { homePosition }
                        world { player.world }
                        members { emptyList() }
                        chunks { emptyList() }
                        flags { emptyList() }
                    }

                    session.persist(land)
                    transaction?.commit()
                    addChunkPlaceholder(chunk, land)
                }
            } catch (e: HibernateException) {
                transaction?.rollback()
                pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot update land", e)
                Sentry.captureException(e)
            }
        }
    }

    fun unclaimLand(player: Player) {

        val land = pandorasClusterApi.getLandService().getLand(player) ?: return
        var transaction: Transaction? = null
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()

                session.remove(land.homePosition)
                land.landMembers.forEach(this::removeLandMember)

                val world = Bukkit.getWorld(land.world)
                if(world != null) {
                    land.chunks.forEach { chunkPlaceholder ->
                        removeChunkPlaceholder(chunkPlaceholder)
                        pandorasClusterApi.getLandService().landCache.invalidate(world.getChunkAt(chunkPlaceholder.chunkIndex))
                    }
                }

                land.flags.forEach(this::removeLandFlag)
                session.remove(land)
                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot remove the land from the database.", e)
            Sentry.captureException(e)
        }
    }

    fun removeChunkPlaceholder(chunkPlaceholder: ChunkPlaceholder) {
        var transaction: Transaction? = null
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.remove(chunkPlaceholder)
                transaction?.commit()

                if(chunkPlaceholder.land != null) {
                    pandorasClusterApi.getLandService().updateLoadedChunks(chunkPlaceholder.land)
                }
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot remove the landmember from the land.", e)
            Sentry.captureException(e)
        }
    }

    fun removeLandFlag(landFlagEntity: LandFlagEntity) {
        var transaction: Transaction? = null
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.remove(landFlagEntity)
                transaction?.commit()

                if(landFlagEntity.land != null) {
                    pandorasClusterApi.getLandService().updateLoadedChunks(landFlagEntity.land)
                }
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot remove the landmember from the land.", e)
            Sentry.captureException(e)
        }
    }

    fun removeLandMember(member: LandMember) {
        var transaction: Transaction? = null
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.remove(member)
                transaction?.commit()
                pandorasClusterApi.getLandService().updateLoadedChunks(member.land)
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot remove the landmember from the land.", e)
            Sentry.captureException(e)
        }
    }
}