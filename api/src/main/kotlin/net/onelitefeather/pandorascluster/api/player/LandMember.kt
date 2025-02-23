package net.onelitefeather.pandorascluster.api.player

import net.onelitefeather.pandorascluster.api.enums.LandRole

data class LandMember(val id: Long?, val member: LandPlayer, val role: LandRole) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LandMember) return false

        if (id != other.id) return false
        if (member != other.member) return false
        if (role != other.role) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + member.hashCode()
        result = 31 * result + role.hashCode()
        return result
    }

    override fun toString(): String {
        return "LandMember(id=$id, member=$member, role=$role)"
    }
}