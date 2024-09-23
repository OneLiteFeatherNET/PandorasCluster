package net.onelitefeather.pandorascluster.api.land

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk
import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.api.land.flag.FlagRoleAttachment
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.api.player.LandMember
import net.onelitefeather.pandorascluster.api.player.LandPlayer
import net.onelitefeather.pandorascluster.api.position.HomePosition
import net.onelitefeather.pandorascluster.api.utils.PlayerUtil
import java.util.*

data class Land(
    val id: Long? = null,
    val owner: LandPlayer?,
    val home: HomePosition?,
    val members: List<LandMember>,
    val chunks: List<ClaimedChunk>,
    val flags: List<FlagRoleAttachment>,
    val world: String = "world"
) {

    constructor() : this(null, null, null, emptyList(), emptyList(), emptyList(), "world")

    fun isOwner(uuid: UUID): Boolean = owner?.uniqueId == uuid

    fun isChunkMerged(chunkIndex: Long): Boolean = chunks.firstOrNull { it.chunkIndex == chunkIndex } != null

    fun isChunkMerged(chunk: ClaimedChunk): Boolean = isChunkMerged(chunk.chunkIndex)

    fun hasMemberAccess(uuid: UUID): Boolean {
        if (isOwner(uuid)) return true
        val landMember = getLandMember(uuid) ?: return false
        if (landMember.role == LandRole.MEMBER && !isAdminOnline()) return false
        return landMember.role.access
    }

    fun hasMemberRole(uuid: UUID, role: LandRole): Boolean =
        members.firstOrNull { it.member.uniqueId == uuid && it.role == role } != null

    fun getLandMember(uuid: UUID): LandMember? =
        members.firstOrNull { landMember -> landMember.member.uniqueId == uuid }

    fun isBanned(uuid: UUID): Boolean {
        if (isOwner(uuid)) return false
        return hasMemberRole(uuid, LandRole.BANNED)
    }

    fun isAdmin(uuid: UUID): Boolean {
        if (isOwner(uuid)) return true
        return hasMemberRole(uuid, LandRole.ADMIN)
    }

    fun getFlag(role: LandRole, flag: LandFlag): FlagRoleAttachment {
        return flags.firstOrNull { it.role == role && it.flag == flag } ?: FlagRoleAttachment.getDefaultFlag(flag)
    }

    fun getFlag(flag: LandFlag): FlagRoleAttachment = getFlag(LandRole.VISITOR, flag)

    private fun isAdminOnline(): Boolean {
        if(owner == null) return false
        if (PlayerUtil.Instances.instance.isOnline(owner.uniqueId)) return true
        return members.any { it.role == LandRole.ADMIN && PlayerUtil.Instances.instance.isOnline(it.member.uniqueId) }
    }
}