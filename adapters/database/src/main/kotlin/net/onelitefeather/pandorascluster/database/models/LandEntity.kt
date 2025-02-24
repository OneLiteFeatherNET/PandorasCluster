package net.onelitefeather.pandorascluster.database.models

import jakarta.persistence.*
import net.onelitefeather.pandorascluster.database.models.flag.FlagRoleAttachmentEntity
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity
import net.onelitefeather.pandorascluster.database.models.position.HomePositionEntity
import net.onelitefeather.pandorascluster.dbo.chunk.ClaimedChunkDBO
import net.onelitefeather.pandorascluster.dbo.flag.FlagRoleAttachmentDBO
import net.onelitefeather.pandorascluster.dbo.land.LandDBO
import net.onelitefeather.pandorascluster.dbo.player.LandMemberDBO
import net.onelitefeather.pandorascluster.dbo.player.LandPlayerDBO
import net.onelitefeather.pandorascluster.dbo.position.HomePositionDBO

@Entity
@Table(name = "lands")
data class LandEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne
    val owner: LandPlayerEntity? = null,

    @OneToOne
    val home: HomePositionEntity? = null,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "land")
    val members: List<LandMemberEntity> = arrayListOf(),

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "landEntity")
    val chunks: List<ClaimedChunkEntity> = arrayListOf(),

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "land")
    val flags: List<FlagRoleAttachmentEntity> = arrayListOf(),

    @Column
    val world: String = "world"
) : LandDBO {

    @Override
    override fun toString(): String {
        return this::class.simpleName +
                "(id = $id , owner = $owner , homePosition = $home , " +
                "world = $world)"
    }

    override fun id(): Long? = id

    override fun owner(): LandPlayerDBO = owner!!

    override fun home(): HomePositionDBO = home!!

    override fun members(): List<LandMemberDBO> = members

    override fun chunks(): List<ClaimedChunkDBO> = chunks

    override fun flags(): List<FlagRoleAttachmentDBO> = flags

    override fun world(): String = world

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LandEntity) return false

        if (id != other.id) return false
        if (owner != other.owner) return false
        if (home != other.home) return false
        if (members != other.members) return false
        if (chunks != other.chunks) return false
        if (flags != other.flags) return false
        if (world != other.world) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (owner?.hashCode() ?: 0)
        result = 31 * result + (home?.hashCode() ?: 0)
        result = 31 * result + members.hashCode()
        result = 31 * result + chunks.hashCode()
        result = 31 * result + flags.hashCode()
        result = 31 * result + world.hashCode()
        return result
    }
}