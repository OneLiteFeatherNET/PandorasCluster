package net.onelitefeather.pandorascluster.service

import io.sentry.Sentry
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.builder.landBuilder
import net.onelitefeather.pandorascluster.enums.LandRole
import net.onelitefeather.pandorascluster.land.ChunkPlaceholder
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.land.player.LandMember
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.land.position.HomePosition
import net.onelitefeather.pandorascluster.land.position.toHomePosition
import org.bukkit.Chunk
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

    fun updateLandFlag(landFlag: LandFlag, value: String, land: Land) {

        var transaction: Transaction? = null
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                if (!pandorasClusterApi.getLandFlagService().existsFlagInLand(landFlag, land)) {
                    session.persist(LandFlagEntity(null, landFlag.name, value, landFlag.type, landFlag.landFlagType, land))
                } else {
                    session.merge(pandorasClusterApi.getLandFlag(landFlag, land)?.copy(value = value))
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
                    pandorasClusterApi.getLandService().claimChunk(chunk, land)
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
        if(pandorasClusterApi.hasPlayerLand(player.uniqueId)) return

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

    fun deletePlayerLand(player: Player) {
        val landPlayer = pandorasClusterApi.getLandPlayer(player.uniqueId) ?: return
        val land = pandorasClusterApi.getLand(landPlayer)
        if (land != null) {
            var transaction: Transaction? = null
            try {
                pandorasClusterApi.getSessionFactory().openSession().use { session ->
                    transaction = session.beginTransaction()
                    session.remove(land)
                    transaction?.commit()
                }
            } catch (e: HibernateException) {
                transaction?.rollback()
                pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot remove the land from the database.", e)
                Sentry.captureException(e)
            }
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