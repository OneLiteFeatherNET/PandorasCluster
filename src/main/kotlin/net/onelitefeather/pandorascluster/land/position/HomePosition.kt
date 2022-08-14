package net.onelitefeather.pandorascluster.land.position

import jakarta.persistence.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
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

    companion object {

        @JvmStatic
        fun fromHomePosition(world: World, homePosition: HomePosition): Location {
            return Location(
                world,
                homePosition.posX,
                homePosition.posY,
                homePosition.posZ,
                homePosition.yaw,
                homePosition.pitch
            )
        }

        @JvmStatic
        fun of(location: Location): HomePosition =
            HomePosition(null, location.x, location.y, location.z, location.yaw, location.pitch)

        @JvmStatic
        fun dummyLocation(): HomePosition = of(Bukkit.getWorlds()[0].spawnLocation)
    }
}
