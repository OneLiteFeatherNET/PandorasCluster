package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.getHighestClaimLimit
import net.onelitefeather.pandorascluster.extensions.miniMessage
import net.onelitefeather.pandorascluster.util.AVAILABLE_CHUNK_ROTATIONS
import net.onelitefeather.pandorascluster.util.IGNORE_CLAIM_LIMIT
import net.onelitefeather.pandorascluster.util.hasSameOwner
import org.bukkit.Chunk
import org.bukkit.entity.Player

class ClaimCommand(private val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land claim")
    @CommandDescription("Claim a free chunk")
    fun execute(player: Player) {

        val pluginPrefix = pandorasClusterApi.pluginPrefix()

        val landPlayer = pandorasClusterApi.getLandPlayer(player.uniqueId)
        if (landPlayer == null) {
            player.sendMessage(miniMessage { "<lang:player-data-not-found:'${pluginPrefix}':'${player.name}'>" })
            return
        }

        val playerChunk = player.chunk
        if (pandorasClusterApi.getLandService().checkWorldGuardRegion(playerChunk)) {
            player.sendMessage(miniMessage { "<lang:worldguard-region-found:'${pluginPrefix}'>" })
            return
        }

        if (pandorasClusterApi.isChunkClaimed(playerChunk)) {
            player.sendMessage(miniMessage { "<lang:chunk-already-claimed:'${pluginPrefix}'>" })
            return
        }

        val chunkX = playerChunk.x
        val chunkZ = playerChunk.z

        pandorasClusterApi.getLandService().findConnectedChunk(player) {
            if (it != null) {

                var claimedChunk: Chunk? = null

                for(facing in AVAILABLE_CHUNK_ROTATIONS) {
                    if (claimedChunk != null) continue
                    val chunk = player.world.getChunkAt(facing.modX + chunkX, facing.modZ + chunkZ)
                    if (pandorasClusterApi.isChunkClaimed(chunk)) {
                        claimedChunk = chunk
                    }
                }

                if (claimedChunk != null) {
                    val claimedLand = pandorasClusterApi.getLand(claimedChunk)
                    if (claimedLand != null && !hasSameOwner(it, claimedLand)) {
                        player.sendMessage(miniMessage { "<lang:another-land-too-close:'${pluginPrefix}'>" })
                        return@findConnectedChunk
                    }
                }

                if (!it.isOwner(player.uniqueId)) {
                    player.sendMessage(miniMessage { "<lang:invalid-land-owner:'${pluginPrefix}'>" })
                    return@findConnectedChunk
                }

                val claimLimit = player.getHighestClaimLimit()

                // Add 1 to the current chunk count and check if the player can claim more chunks
                val newChunkCount = (pandorasClusterApi.getLandService().getChunksByLand(it) + 1)

                if(claimLimit != IGNORE_CLAIM_LIMIT && newChunkCount > claimLimit) {
                    player.sendMessage(miniMessage { "<lang:chunk.claim-limit-reached:'${pluginPrefix}'>" })
                    return@findConnectedChunk
                }

                this.pandorasClusterApi.getDatabaseStorageService().addChunkPlaceholder(playerChunk, it)
                player.sendMessage(miniMessage { "<lang:chunk-successfully-merged:'${pluginPrefix}'>" })
            } else {
                if (pandorasClusterApi.hasPlayerLand(player)) {
                    player.sendMessage(miniMessage { "<lang:player-already-has-land:'${pluginPrefix}'>" })
                } else {
                    pandorasClusterApi.getDatabaseStorageService().createLand(landPlayer, player, playerChunk)
                    player.sendMessage(miniMessage { "<lang:chunk-successfully-claimed:'${pluginPrefix}'>" })
                }
            }
        }
    }
}