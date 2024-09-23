package net.onelitefeather.pandorascluster.database.models.player

import jakarta.persistence.*
import net.onelitefeather.pandorascluster.api.utils.SERVER_UUID

@Entity
@Table(name = "land_players")
data class LandPlayerEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(columnDefinition = "VARCHAR(36)")
    val uuid: String = SERVER_UUID.toString(),

    @Column
    val name: String? = null
) {

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , uuid = $uuid , name = $name )"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LandPlayerEntity) return false

        if (id != other.id) return false
        if (uuid != other.uuid) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode() ?: 0
        result = 31 * result + uuid.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }
}