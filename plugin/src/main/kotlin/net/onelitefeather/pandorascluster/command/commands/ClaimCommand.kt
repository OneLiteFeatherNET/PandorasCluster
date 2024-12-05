package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.PandorasClusterPlugin
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk
import net.onelitefeather.pandorascluster.api.utils.IGNORE_CLAIM_LIMIT
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import net.onelitefeather.pandorascluster.extensions.LocationUtils
import net.onelitefeather.pandorascluster.util.AVAILABLE_CHUNK_ROTATIONS
import net.onelitefeather.pandorascluster.util.PLUGIN_PREFIX
import org.bukkit.Chunk
import org.bukkit.entity.Player

class ClaimCommand(
    private val pandorasClusterApi: PandorasClusterApi,
    private val plugin: PandorasClusterPlugin
) : EntityUtils, ChunkUtils, LocationUtils {

    @CommandMethod("land claim")
    @CommandDescription("Claim a free chunk")
    fun execute(player: Player) {


        val landPlayer = pandorasClusterApi.getLandPlayerService().getLandPlayer(player.uniqueId)
        if (landPlayer == null) {
            player.sendMessage(
                Component.translatable("player-data-not-found")
                    .arguments(PLUGIN_PREFIX, Component.text(player.name))
            )
            return
        }

        val playerChunk = player.chunk
        if (plugin.bukkitLandService.checkWorldGuardRegion(playerChunk)) {
            player.sendMessage(Component.translatable("worldguard-region-found").arguments(PLUGIN_PREFIX))
            return
        }

        if (pandorasClusterApi.getLandService().isChunkClaimed(playerChunk.chunkKey)) {
            player.sendMessage(Component.translatable("chunk-already-claimed").arguments(PLUGIN_PREFIX))
            return
        }

        val chunkX = playerChunk.x
        val chunkZ = playerChunk.z

        plugin.bukkitLandService.findConnectedChunk(player) {
            if (it != null) {

                var claimedChunk: Chunk? = null

                for (facing in AVAILABLE_CHUNK_ROTATIONS) {
                    if (claimedChunk != null) continue
                    val chunk = player.world.getChunkAt(facing.modX + chunkX, facing.modZ + chunkZ)
                    if (pandorasClusterApi.getLandService().isChunkClaimed(chunk.chunkKey)) {
                        claimedChunk = chunk
                    }
                }

                if (claimedChunk != null) {
                    val claimedLand = pandorasClusterApi.getLandService().getLand(claimedChunk.chunkKey)
                    if (claimedLand != null && !hasSameOwner(it, claimedLand)) {
                        player.sendMessage(Component.translatable("another-land-too-close").arguments(PLUGIN_PREFIX))
                        return@findConnectedChunk
                    }
                }

                if (!it.isOwner(player.uniqueId)) {
                    player.sendMessage(Component.translatable("invalid-land-owner").arguments(PLUGIN_PREFIX))
                    return@findConnectedChunk
                }

                val claimLimit = getHighestClaimLimit(player)

                // Add 1 to the current chunk count and check if the player can claim more chunks
                val newChunkCount = (it.chunks.size + 1)

                if (claimLimit != IGNORE_CLAIM_LIMIT && newChunkCount > claimLimit) {
                    player.sendMessage(Component.translatable("chunk.claim-limit-reached").arguments(PLUGIN_PREFIX))
                    return@findConnectedChunk
                }

                this.pandorasClusterApi.getLandService().addClaimedChunk(ClaimedChunk(null, playerChunk.chunkKey), it)
                player.sendMessage(Component.translatable("chunk-successfully-merged").arguments(PLUGIN_PREFIX))
            } else {
                if (pandorasClusterApi.getLandService().hasPlayerLand(landPlayer)) {
                    player.sendMessage(Component.translatable("player-already-has-land").arguments(PLUGIN_PREFIX))
                } else {
                    pandorasClusterApi.getDatabaseStorageService().createLand(
                        landPlayer,
                        toHomePosition(player.location),
                        ClaimedChunk(null, playerChunk.chunkKey),
                        player.world.name)
                    player.sendMessage(Component.translatable("chunk-successfully-claimed").arguments(PLUGIN_PREFIX))
                }
            }
        }
    }
}
