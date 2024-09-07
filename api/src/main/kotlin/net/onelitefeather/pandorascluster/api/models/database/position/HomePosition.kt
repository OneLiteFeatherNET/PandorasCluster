package net.onelitefeather.pandorascluster.api.models.database.position

import jakarta.persistence.*
import net.onelitefeather.pandorascluster.api.models.dto.PandorasLocation
import net.onelitefeather.pandorascluster.api.utils.LocationUtil
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

    fun getBlockX(): Int = LocationUtil.Instances.instance.locToBlock(this.posX)

    fun getBlockY(): Int = LocationUtil.Instances.instance.locToBlock(this.posY)

    fun getBlockZ(): Int = LocationUtil.Instances.instance.locToBlock(this.posZ)

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
fun toHomePosition(location: PandorasLocation): HomePosition =
    HomePosition(null, location.x(), location.y(), location.z(), location.yaw(), location.pitch())