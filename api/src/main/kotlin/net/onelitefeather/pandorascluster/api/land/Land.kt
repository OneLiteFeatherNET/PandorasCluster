package net.onelitefeather.pandorascluster.api.land

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk
import net.onelitefeather.pandorascluster.api.enums.LandRole
import net.onelitefeather.pandorascluster.api.enums.Permission
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

    fun hasMemberAccess(uuid: UUID, flag: LandFlag): Boolean {

        if (isOwner(uuid)) return true

        val hasVisitorAccess = hasVisitorAccess(flag)

        val member = getLandMember(uuid) ?: return hasVisitorAccess
        val landFlag = getFlag(member.role, flag) ?: return hasVisitorAccess

        val checkLandFlag = flag == landFlag.flag
        val hasMemberRoleAccess = member.role == LandRole.MEMBER && checkLandFlag && !isAdminOnline()

        if(hasMemberRoleAccess) return false
        if(checkLandFlag) return true

        val flagPermission = Permission.FLAG_PERMISSION.permissionNode.format(flag.name)
        return PlayerUtil.Instances.instance.hasPermission(uuid, flagPermission)
    }

    fun hasMemberRole(uuid: UUID, role: LandRole): Boolean =
        members.firstOrNull { it.member.uniqueId == uuid && it.role == role } != null

    fun getLandMember(uuid: UUID): LandMember? =
        members.firstOrNull { landMember -> landMember.member.uniqueId == uuid }

    fun isBanned(uuid: UUID): Boolean {
        if(PlayerUtil.Instances.instance.hasPermission(uuid, Permission.LAND_ENTRY_DENIED)) return false
        if (isOwner(uuid)) return false
        return hasMemberRole(uuid, LandRole.BANNED)
    }

    fun isAdmin(uuid: UUID): Boolean {
        if (isOwner(uuid)) return true
        return hasMemberRole(uuid, LandRole.ADMIN)
    }

    fun getFlag(flag: LandFlag): FlagRoleAttachment? {
        return flags.firstOrNull { it.flag == flag }
    }

    fun getFlag(role: LandRole, flag: LandFlag): FlagRoleAttachment? {
        return flags.firstOrNull { it.role == role && it.flag == flag }
    }

    fun getFlags(role: LandRole): List<FlagRoleAttachment> = flags.filter { it.role == role }

    fun getDefaultFlag(flag: LandFlag): FlagRoleAttachment {
        return FlagRoleAttachment(null, LandRole.VISITOR, flag)
    }

    fun hasFlag(landFlag: LandFlag): Boolean = flags.any { it.flag == landFlag }

    fun hasVisitorAccess(flagRoleAttachment: FlagRoleAttachment): Boolean = hasVisitorAccess(flagRoleAttachment.flag)

    private fun isAdminOnline(): Boolean {
        if (owner == null) return false
        if (PlayerUtil.Instances.instance.isOnline(owner.uniqueId)) return true
        return members.any { it.role == LandRole.ADMIN && PlayerUtil.Instances.instance.isOnline(it.member.uniqueId) }
    }

    private fun hasVisitorAccess(landFlag: LandFlag): Boolean {
        val flag = getDefaultFlag(landFlag)
        return flag.role == LandRole.VISITOR
    }

    override fun toString(): String {
        return "Land(id=$id, owner=$owner, home=$home, members=$members, chunks=$chunks, flags=$flags, world='$world')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Land) return false

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