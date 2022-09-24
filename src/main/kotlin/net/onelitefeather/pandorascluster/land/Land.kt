package net.onelitefeather.pandorascluster.land

import jakarta.persistence.*
import net.onelitefeather.pandorascluster.enums.LandRole
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.land.player.LandMember
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.land.position.HomePosition
import net.onelitefeather.pandorascluster.land.position.dummyHomePosition
import org.bukkit.Bukkit
import org.hibernate.Hibernate
import java.util.*

@Entity
data class Land(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne
    val owner: LandPlayer? = null,

    @OneToOne
    val homePosition: HomePosition = dummyHomePosition(),

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "land")
    val landMembers: List<LandMember> = arrayListOf(),

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "land")
    val chunks: List<ChunkPlaceholder> = arrayListOf(),

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "land")
    val flags: List<LandFlagEntity> = arrayListOf(),

    @Column
    val world: String = "world",

    @Column
    val x: Int = -1,

    @Column
    val z: Int = -1
) {

    fun isOwner(uniqueId: UUID): Boolean {
        return owner?.getUniqueId() == uniqueId
    }

    fun getLandFlag(landFlag: LandFlag): LandFlagEntity? =
        flags.firstOrNull { landFlagEntity -> landFlagEntity.name?.uppercase() == landFlag.name.uppercase() }

    fun getMergedChunk(chunkIndex: Long): ChunkPlaceholder? =
        chunks.firstOrNull { chunkPlaceholder -> chunkPlaceholder.chunkIndex == chunkIndex }

    fun getLandMember(uuid: UUID): LandMember? =
        landMembers.firstOrNull { landMember -> landMember.member?.getUniqueId() == uuid }

    fun hasMemberPermission(memberId: UUID, permission: Permission): Boolean {
        val bukkitPlayer = Bukkit.getPlayer(memberId) ?: return false
        return bukkitPlayer.hasPermission(permission)
    }

    fun hasAccess(uuid: UUID): Boolean {
        if(isOwner(uuid)) return true
        if(hasMemberPermission(uuid, Permission.ACCESS)) return true
        val landOwner = owner ?: return false
        val landMember = getLandMember(uuid) ?: return false
        if (landMember.role == LandRole.MEMBER && !landOwner.isOnline()) return false
        return landMember.role.access
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Land

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName +
                "(id = $id , owner = $owner , homePosition = $homePosition , " +
                "world = $world , x = $x , z = $z )"
    }

    fun isBanned(uniqueId: UUID): Boolean {
        val member = getLandMember(uniqueId) ?: return false
        return member.role == LandRole.BANNED
    }

    fun isMerged() = chunks.isNotEmpty()
}