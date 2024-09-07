package net.onelitefeather.pandorascluster.land.player

import jakarta.persistence.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.hibernate.Hibernate
import java.util.*

@Entity
data class LandPlayer(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(columnDefinition = "VARCHAR(36)")
    val uuid: String? = null,

    @Column
    val name: String? = null
) {

    fun getUniqueId(): UUID? = if (uuid == null) null else UUID.fromString(uuid)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as LandPlayer
        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , uuid = $uuid , name = $name )"
    }

    fun isOnline(): Boolean {
        return getBukkitPlayer() != null
    }

    fun getBukkitPlayer(): Player? {
        val playerId = getUniqueId() ?: return null
        return Bukkit.getPlayer(playerId)
    }
}
