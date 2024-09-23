package net.onelitefeather.pandorascluster.api.player

import java.util.UUID

data class LandPlayer(val id: Long?, val uniqueId: UUID, val name: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LandPlayer) return false

        if (id != other.id) return false
        if (uniqueId != other.uniqueId) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + uniqueId.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "LandPlayer(id=$id, uniqueId=$uniqueId, name='$name')"
    }


}