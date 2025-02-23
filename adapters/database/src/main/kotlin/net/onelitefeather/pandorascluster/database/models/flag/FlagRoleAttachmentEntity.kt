package net.onelitefeather.pandorascluster.database.models.flag

import jakarta.persistence.*
import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.database.models.LandEntity
import net.onelitefeather.pandorascluster.dbo.flag.FlagRoleAttachmentDBO

@Entity
@Table(name = "land_flags")
data class FlagRoleAttachmentEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column
    @Enumerated(EnumType.STRING)
    val role: LandRole,

    @Column
    @Enumerated(EnumType.STRING)
    val landFlag: LandFlag = LandFlag.UNKNOWN,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_id")
    val land: LandEntity? = null
) : FlagRoleAttachmentDBO {

    constructor() : this(null, LandRole.VISITOR, LandFlag.UNKNOWN)

    override fun id(): Long? = id

    override fun role(): LandRole = role

    override fun flag(): LandFlag = landFlag

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FlagRoleAttachmentEntity) return false

        if (id != other.id) return false
        if (landFlag != other.landFlag) return false
        if (role != other.role) return false
        if (land != other.land) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + landFlag.hashCode()
        result = 31 * result + role.hashCode()
        result = 31 * result + (land?.hashCode() ?: 0)
        return result
    }
}