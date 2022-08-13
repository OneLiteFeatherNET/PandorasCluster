package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.miniMessage
import net.onelitefeather.pandorascluster.util.ChunkUtil
import org.bukkit.Chunk
import org.bukkit.entity.Player

@Suppress("kotlin:S3776")
class ClaimCommand(private val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land claim")
    @CommandDescription("Claim a free chunk")
    fun execute(player: Player) {

        val landPlayer = this.pandorasClusterApi.getLandPlayer(player.uniqueId)
        if (landPlayer == null) {
            player.sendMessage(miniMessage { "Could not find your player data." })
            return
        }

        val playerChunk = player.chunk
        if (pandorasClusterApi.isChunkClaimed(playerChunk)) {
            player.sendMessage(miniMessage { "This chunk was already claimed!" })
            return
        }

        pandorasClusterApi.getLandService().findConnectedChunk(player) {

            val chunkX = playerChunk.x
            val chunkZ = playerChunk.z
            var claimedChunk: Chunk? = null

            var x = -2
            while (x < 2 && claimedChunk == null) {
                var z = -2
                while (z < 2 && claimedChunk == null) {
                    val chunk = player.world.getChunkAt(x + chunkX, z + chunkZ)
                    if (pandorasClusterApi.isChunkClaimed(chunk)) {
                        claimedChunk = chunk
                    }
                    z++
                }
                x++
            }

            if(it != null) {

                if (claimedChunk != null) {
                    val claimedLand = pandorasClusterApi.getLand(claimedChunk)
                    if (claimedLand != null && !ChunkUtil.hasSameOwner(it, claimedLand)) {
                        player.sendMessage(miniMessage { "This is not allowed to claim!" })
                        return@findConnectedChunk
                    }
                }

                if (!it.isOwner(player.uniqueId)) {
                    player.sendMessage(miniMessage { "You´re not the Owner from this Land!" })
                    return@findConnectedChunk
                }

                this.pandorasClusterApi.getDatabaseStorageService().addChunkPlaceholder(playerChunk, it)
                player.sendMessage(miniMessage { "You´ve successfully merged this land!" })
            } else {
                if (pandorasClusterApi.hasPlayerLand(player)) {
                    player.sendMessage(miniMessage { "Du besitzt bereits schon ein Land." })
                } else {
                    pandorasClusterApi.getDatabaseStorageService().createLand(landPlayer, player, playerChunk)
                    player.sendMessage(miniMessage { "You´ve successfully claimed this land." })
                }
            }

        }
    }
}