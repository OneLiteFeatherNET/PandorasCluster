package net.onelitefeather.pandorascluster.service

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.builder.landBuilder
import net.onelitefeather.pandorascluster.enums.LandRole
import net.onelitefeather.pandorascluster.land.ChunkPlaceholder
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.land.flag.LandFlagType
import net.onelitefeather.pandorascluster.land.player.LandMember
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.land.position.HomePosition
import org.bukkit.Chunk
import org.bukkit.entity.Player
import org.hibernate.HibernateException
import org.hibernate.Transaction
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.logging.Level
import net.onelitefeather.pandorascluster.land.flag.LAND_FLAGS
import net.onelitefeather.pandorascluster.util.getChunkIndex

class DatabaseStorageService(private val pandorasClusterApi: PandorasClusterApi) {

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
        }
    }

    fun addLandMember(land: Land, member: LandPlayer, landRole: LandRole?) {
        val landMember = LandMember(null, member, landRole ?: LandRole.MEMBER, land)
        var transaction: Transaction? = null
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()

                val storedMember = pandorasClusterApi.getLandService().getLandMember(land, member)
                if (storedMember != null) {
                    session.merge(storedMember.copy(role = landRole ?: LandRole.MEMBER, member = member, land = land))
                } else {
                    session.persist(landMember)
                }

                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.getLogger().log(
                Level.SEVERE,
                "Cannot save land member $landMember",
                e
            )
        }
    }

    fun setLandOwner(land: Land, landPlayer: LandPlayer) {
        updateLand(land.copy(owner = landPlayer))
    }

    fun updateLandFlag(landFlagEntity: LandFlagEntity) {
        var transaction: Transaction? = null
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.merge(landFlagEntity)
                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.getLogger()
                .log(Level.SEVERE, String.format("Cannot update landflag %s", landFlagEntity), e)
        }
    }

    fun updateLand(land: Land) {
        var transaction: Transaction? = null
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.merge(land)
                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.getLogger().log(Level.SEVERE, String.format("Cannot update land %s", land), e)
        }
    }

    fun addChunkPlaceholder(chunk: Chunk?, land: Land?) {
        var transaction: Transaction? = null
        val chunkPlaceholder = ChunkPlaceholder(null, getChunkIndex(chunk!!), land)
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()
                session.persist(chunkPlaceholder)
                transaction?.commit()
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.getLogger().log(
                Level.SEVERE,
                "Cannot save chunk placeholder $chunkPlaceholder",
                e
            )
        }
    }

    private fun addFlags(land: Land) {
        CompletableFuture.runAsync {
            val flagEntities: MutableList<LandFlagEntity> = ArrayList()
            for (landFlag in LAND_FLAGS) {

                if (landFlag.landFlagType == LandFlagType.UNKNOWN) continue
                flagEntities.add(
                    LandFlagEntity(
                        null,
                        landFlag.name,
                        landFlag.defaultValue.toString(),
                        landFlag.type,
                        landFlag.landFlagType,
                        land
                    )
                )
            }
            var transaction: Transaction? = null
            try {
                pandorasClusterApi.getSessionFactory().openSession().use { session ->

                    transaction = session.beginTransaction()
                    for (landFlag in flagEntities) {
                        session.persist(landFlag)
                    }

                    transaction?.commit()
                }
            } catch (e: HibernateException) {
                transaction?.rollback()
                pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot add flags to land $land", e)
            }
        }
    }

    fun createLand(owner: LandPlayer, player: Player, chunk: Chunk) {
        if (!pandorasClusterApi.getLandService().hasPlayerLand(player.uniqueId)) {
            var transaction: Transaction? = null
            try {
                pandorasClusterApi.getSessionFactory().openSession().use { session ->
                    transaction = session.beginTransaction()

                    val homePosition = HomePosition.of(player.location)
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

                    addFlags(land)
                    transaction?.commit()
                    addChunkPlaceholder(chunk, land)
                }
            } catch (e: HibernateException) {
                transaction?.rollback()
                pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot update land", e)
            }
        }
    }

    fun deletePlayerLand(player: Player) {
        val landPlayer = pandorasClusterApi.getLandPlayer(player.uniqueId) ?: return
        val land: Land = pandorasClusterApi.getLandService().getLand(landPlayer) ?: return
        if (pandorasClusterApi.getLandService().exists(land)) {
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
            }
        } catch (e: HibernateException) {
            transaction?.rollback()
            pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot remove the land from the database.", e)
        }
    }
}