package net.onelitefeather.pandorascluster.api.chunk

class ClaimedChunk(val id: Long? = -1L, val chunkIndex: Long) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClaimedChunk) return false

        if (id != other.id) return false
        if (chunkIndex != other.chunkIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + chunkIndex.hashCode()
        return result
    }

    override fun toString(): String {
        return "ClaimedChunk(id=$id, chunkIndex=$chunkIndex)"
    }


}