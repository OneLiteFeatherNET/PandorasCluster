package net.onelitefeather.pandorascluster.api.land.flag

import net.onelitefeather.pandorascluster.api.enums.LandRole

data class FlagRoleAttachment(
    val id: Long?,
    val role: LandRole = LandRole.MEMBER,
    val flag: LandFlag = LandFlag.UNKNOWN
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FlagRoleAttachment) return false

        if (id != other.id) return false
        if (role != other.role) return false
        if (flag != other.flag) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + role.hashCode()
        result = 31 * result + flag.hashCode()
        return result
    }

    override fun toString(): String {
        return "FlagRoleAttachment(id=$id, role=$role, flag=$flag)"
    }
}