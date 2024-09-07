package net.onelitefeather.pandorascluster.api.models.database

import jakarta.persistence.*
import org.hibernate.Hibernate

@Entity
data class ChunkPlaceholder(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column
    val chunkIndex: Long = -1,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_id")
    val land: Land? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ChunkPlaceholder
        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , chunkIndex = $chunkIndex )"
    }
}