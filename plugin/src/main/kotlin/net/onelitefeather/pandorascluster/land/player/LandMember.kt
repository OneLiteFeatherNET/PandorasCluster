package net.onelitefeather.pandorascluster.land.player

import jakarta.persistence.*
import net.onelitefeather.pandorascluster.api.models.database.player.LandMember
import net.onelitefeather.pandorascluster.enums.LandRole
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.util.DUMMY_LAND
import org.hibernate.Hibernate

@Entity
data class LandMember(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne
    val member: LandPlayer? = null,

    @Enumerated(EnumType.STRING)
    val role: LandRole = LandRole.VISITOR,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_id")
    val land: Land = DUMMY_LAND

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as LandMember

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , member = $member , role = $role )"
    }
}