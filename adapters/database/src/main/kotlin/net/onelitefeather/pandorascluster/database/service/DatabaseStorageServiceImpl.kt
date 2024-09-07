package net.onelitefeather.pandorascluster.database.service

import net.onelitefeather.pandorascluster.api.enum.LandRole
import net.onelitefeather.pandorascluster.api.models.*
import net.onelitefeather.pandorascluster.api.service.DatabaseStorageService
import org.hibernate.HibernateException
import org.hibernate.Transaction
import java.util.*
import java.util.logging.Level

class DatabaseStorageServiceImpl : DatabaseStorageService {

    override fun updateLandHome(homePosition: HomePosition, ownerId: UUID) {
        var transaction: Transaction? = null
        try {
            pandorasClusterApi.getSessionFactory().openSession().use { session ->
                transaction = session.beginTransaction()

                val stored = pandorasClusterApi.getLandService().getHome(ownerId)
                if (stored != null) {

                    session.merge(
                        stored.copy(
                            id = stored.id,
                            posX = homePosition.posX(),
                            posY = homePosition.posY(),
                            posZ = homePosition.posZ(),
                            yaw = homePosition.yaw(),
                            pitch = homePosition.pitch()
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

    override fun addLandMember(
        land: Land,
        member: LandPlayer,
        landRole: LandRole?
    ) {
        TODO("Not yet implemented")
    }

    override fun setLandOwner(land: Land, landPlayer: LandPlayer) {
        TODO("Not yet implemented")
    }

    override fun addUseMaterial(land: Land, material: String) {
        TODO("Not yet implemented")
    }

    override fun getLandFlag(landFlag: LandFlag, land: Land): LandFlagEntity? {
        TODO("Not yet implemented")
    }

    override fun removeUseMaterial(land: Land, material: String) {
        TODO("Not yet implemented")
    }

    override fun updateLandFlag(landFlag: LandFlag, value: String, land: Land) {
        TODO("Not yet implemented")
    }

    override fun updateLand(land: Land) {
        TODO("Not yet implemented")
    }

    override fun addChunkPlaceholder(chunk: Chunk, land: Land?) {
        TODO("Not yet implemented")
    }

    override fun createLand(owner: LandPlayer, player: Player, chunk: Chunk) {
        TODO("Not yet implemented")
    }

    override fun unclaimLand(land: Land) {
        TODO("Not yet implemented")
    }

    override fun removeChunkPlaceholder(chunkPlaceholder: ChunkPlaceholder) {
        TODO("Not yet implemented")
    }

    override fun removeLandFlag(landFlagEntity: LandFlagEntity) {
        TODO("Not yet implemented")
    }

    override fun removeLandMember(member: LandMember) {
        TODO("Not yet implemented")
    }
}