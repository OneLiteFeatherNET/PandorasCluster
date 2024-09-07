package net.onelitefeather.pandorascluster.database.models.position

import jakarta.persistence.*
import org.hibernate.Hibernate

@Entity
data class HomePosition(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column
    val posX: Double = 1.0,

    @Column
    val posY: Double = 1.0,

    @Column
    val posZ: Double = 1.0,

    @Column
    val yaw: Float = 1.0F,

    @Column
    val pitch: Float = 1.0F
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as HomePosition

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String =
        this::class.simpleName +
                "(id = $id , posX = $posX , posY = $posY , posZ = $posZ , " +
                "yaw = $yaw , pitch = $pitch)"
}