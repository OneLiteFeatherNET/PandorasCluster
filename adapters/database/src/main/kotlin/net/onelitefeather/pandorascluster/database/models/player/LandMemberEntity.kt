package net.onelitefeather.pandorascluster.database.models.player

import jakarta.persistence.*
import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.database.models.LandEntity
import org.hibernate.Hibernate

@Entity
@Table(name = "land_members")
data class LandMemberEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne
    val member: LandPlayerEntity,

    @Enumerated(EnumType.STRING)
    val role: LandRole = LandRole.VISITOR,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_id")
    val land: LandEntity? = null

) {


    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , member = $member , role = $role )"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LandMemberEntity) return false

        if (id != other.id) return false
        if (member != other.member) return false
        if (role != other.role) return false
        if (land != other.land) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + member.hashCode()
        result = 31 * result + role.hashCode()
        result = 31 * result + (land?.hashCode() ?: 0)
        return result
    }
}