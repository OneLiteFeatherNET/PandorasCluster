package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import net.onelitefeather.pandorascluster.util.AVAILABLE_CHUNK_ROTATIONS
import net.onelitefeather.pandorascluster.util.IGNORE_CLAIM_LIMIT
import org.bukkit.Chunk
import org.bukkit.entity.Player

class ClaimCommand(private val pandorasClusterApi: PandorasClusterApi) : EntityUtils, ChunkUtils {

    @CommandMethod("land claim")
    @CommandDescription("Claim a free chunk")
    fun execute(player: Player) {

        val pluginPrefix = pandorasClusterApi.pluginPrefix()

        val landPlayer = pandorasClusterApi.getLandPlayer(player.uniqueId)
        if (landPlayer == null) {
            player.sendMessage(Component.translatable("player-data-not-found").arguments(
                pluginPrefix, Component.text(player.name)))
            return
        }

        val playerChunk = player.chunk
        if (pandorasClusterApi.getLandService().checkWorldGuardRegion(playerChunk)) {
            player.sendMessage(Component.translatable("worldguard-region-found").arguments(pluginPrefix))
            return
        }

        if (pandorasClusterApi.isChunkClaimed(playerChunk)) {
            player.sendMessage(Component.translatable("chunk-already-claimed").arguments(pluginPrefix))
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
                        player.sendMessage(Component.translatable("another-land-too-close").arguments(pluginPrefix))
                        return@findConnectedChunk
                    }
                }

                if (!it.isOwner(player.uniqueId)) {
                    player.sendMessage(Component.translatable("invalid-land-owner").arguments(pluginPrefix))
                    return@findConnectedChunk
                }

                val claimLimit = getHighestClaimLimit(player)

                // Add 1 to the current chunk count and check if the player can claim more chunks
                val newChunkCount = (pandorasClusterApi.getLandService().getChunksByLand(it) + 1)

                if(claimLimit != IGNORE_CLAIM_LIMIT && newChunkCount > claimLimit) {
                    player.sendMessage(Component.translatable("chunk.claim-limit-reached").arguments(pluginPrefix))
                    return@findConnectedChunk
                }

                this.pandorasClusterApi.getDatabaseStorageService().addChunkPlaceholder(playerChunk, it)
                player.sendMessage(Component.translatable("chunk-successfully-merged").arguments(pluginPrefix))
            } else {
                if (pandorasClusterApi.hasPlayerLand(player)) {
                    player.sendMessage(Component.translatable("player-already-has-land").arguments(pluginPrefix))
                } else {
                    pandorasClusterApi.getDatabaseStorageService().createLand(landPlayer, player, playerChunk)
                    player.sendMessage(Component.translatable("chunk-successfully-claimed").arguments(pluginPrefix))
                }
            }
        }
    }
}