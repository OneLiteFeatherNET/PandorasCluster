package net.onelitefeather.pandorascluster.database.models

import jakarta.persistence.*
import net.onelitefeather.pandorascluster.dbo.chunk.ClaimedChunkDBO

@Entity
@Table(name = "land_chunks")
data class ClaimedChunkEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = -1L,

    @Column
    val chunkIndex: Long = -1,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_id")
    val landEntity: LandEntity? = null
): ClaimedChunkDBO {

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , chunkIndex = $chunkIndex )"
    }

    override fun id(): Long? = id

    override fun chunkIndex(): Long = chunkIndex

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClaimedChunkEntity) return false

        if (id != other.id) return false
        if (chunkIndex != other.chunkIndex) return false
        if (landEntity != other.landEntity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + chunkIndex.hashCode()
        result = 31 * result + (landEntity?.hashCode() ?: 0)
        return result
    }
}