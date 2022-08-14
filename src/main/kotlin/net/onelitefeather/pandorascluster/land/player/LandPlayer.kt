package net.onelitefeather.pandorascluster.land.player

import jakarta.persistence.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
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

    fun getDisplayName(): Component {
        val id = getUniqueId()
        id ?: return LegacyComponentSerializer.legacyAmpersand().deserialize(name ?: "Steve")
        val player =
            Bukkit.getPlayer(id) ?: return LegacyComponentSerializer.legacyAmpersand().deserialize(name ?: "Steve")
        return player.displayName()
    }

    fun getLocation(): Location? {
        val id = getUniqueId()
        id ?: return null
        return Bukkit.getPlayer(id)?.location
    }

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
        val playerId = getUniqueId() ?: return false
        return Bukkit.getPlayer(playerId) != null
    }

}
