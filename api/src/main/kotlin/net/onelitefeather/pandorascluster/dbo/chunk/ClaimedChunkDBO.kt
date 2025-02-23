package net.onelitefeather.pandorascluster.dbo.chunk

interface ClaimedChunkDBO {

    fun id(): Long?

    fun chunkIndex(): Long
}