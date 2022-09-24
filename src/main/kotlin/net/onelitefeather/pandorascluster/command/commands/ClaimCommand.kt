package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.miniMessage
import net.onelitefeather.pandorascluster.util.AVAILABLE_CHUNK_ROTATIONS
import net.onelitefeather.pandorascluster.util.hasSameOwner
import org.bukkit.Chunk
import org.bukkit.entity.Player

@Suppress("kotlin:S3776")
class ClaimCommand(private val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land claim")
    @CommandDescription("Claim a free chunk")
    fun execute(player: Player) {

        val pluginPrefix = pandorasClusterApi.pluginPrefix()

        val landPlayer = pandorasClusterApi.getLandPlayer(player.uniqueId)
        if (landPlayer == null) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n(
                "player-data-not-found", *arrayOf(pluginPrefix, player.name)
            ) })
            return
        }

        val playerChunk = player.chunk
        if (pandorasClusterApi.getLandService().checkWorldGuardRegion(playerChunk)) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("worldguard-region-found", *arrayOf(pluginPrefix)) })
            return
        }

        if (pandorasClusterApi.isChunkClaimed(playerChunk)) {
            player.sendMessage(miniMessage { pandorasClusterApi.i18n("chunk-already-claimed", *arrayOf(pluginPrefix)) })
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
                        player.sendMessage(miniMessage { pandorasClusterApi.i18n("another-land-too-close", *arrayOf(pluginPrefix)) })
                        return@findConnectedChunk
                    }
                }

                if (!it.isOwner(player.uniqueId)) {
                    player.sendMessage(miniMessage { pandorasClusterApi.i18n("invalid-land-owner", *arrayOf(pluginPrefix)) })
                    return@findConnectedChunk
                }

                this.pandorasClusterApi.getDatabaseStorageService().addChunkPlaceholder(playerChunk, it)
                player.sendMessage(miniMessage { pandorasClusterApi.i18n("chunk-successfully-merged", *arrayOf(pluginPrefix)) })
            } else {
                if (pandorasClusterApi.hasPlayerLand(player)) {
                    player.sendMessage(miniMessage { pandorasClusterApi.i18n("player-already-has-land", *arrayOf(pluginPrefix)) })
                } else {
                    pandorasClusterApi.getDatabaseStorageService().createLand(landPlayer, player, playerChunk)
                    player.sendMessage(miniMessage { pandorasClusterApi.i18n("chunk-successfully-claimed", *arrayOf(pluginPrefix)) })
                }
            }
        }
    }
}