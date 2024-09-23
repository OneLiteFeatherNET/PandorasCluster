package net.onelitefeather.pandorascluster.database.models.position

import jakarta.persistence.*

@Entity
@Table(name = "land_homes")
data class HomePositionEntity(

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

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String =
        this::class.simpleName +
                "(id = $id , posX = $posX , posY = $posY , posZ = $posZ , " +
                "yaw = $yaw , pitch = $pitch)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HomePositionEntity) return false

        if (id != other.id) return false
        if (posX != other.posX) return false
        if (posY != other.posY) return false
        if (posZ != other.posZ) return false
        if (yaw != other.yaw) return false
        if (pitch != other.pitch) return false

        return true
    }
}