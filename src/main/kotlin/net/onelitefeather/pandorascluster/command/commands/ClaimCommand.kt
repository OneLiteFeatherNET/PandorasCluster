package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.toMM
import net.onelitefeather.pandorascluster.util.ChunkUtil
import org.bukkit.Chunk
import org.bukkit.entity.Player

class ClaimCommand(private val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land claim")
    @CommandDescription("Claim a free chunk")
    fun execute(player: Player) {

        val landPlayer = this.pandorasClusterApi.getLandPlayer(player.uniqueId)
        if (landPlayer == null) {
            player.sendMessage("Could not find your player data.")
            return
        }

        val playerChunk = player.chunk
        if(pandorasClusterApi.isChunkClaimed(playerChunk)) {
            player.sendMessage("This chunk was already claimed!".toMM())
            return
        }

        pandorasClusterApi.getLandService().findConnectedChunk(player, consumer = {

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
                    val claimedLand = pandorasClusterApi.getLand(claimedChunk!!)
                    if (claimedLand != null && !ChunkUtil.hasSameOwner(it, claimedLand)) {
                        player.sendMessage(Component.text("distance"))
                        return@findConnectedChunk
                    }
                }

                if (!it.isOwner(player.uniqueId)) {
                    player.sendMessage(Component.text("You´re not the Owner from this Land!"))
                    return@findConnectedChunk
                }

                this.pandorasClusterApi.getDatabaseStorageService().addChunkPlaceholder(playerChunk, it)
                player.sendMessage(Component.text("You´ve successfully merged this land!"))
                player.sendMessage(String.format("DEBUG: Connected with Land X: %d Z: %d", it.x, it.z))
            } else {
                if (pandorasClusterApi.hasPlayerLand(player)) {
                    player.sendMessage(Component.text("Du besitzt bereits schon ein Land."))
                } else {
                    pandorasClusterApi.getDatabaseStorageService().createLand(landPlayer, player, playerChunk)
                    player.sendMessage(Component.text("You´ve successfully claimed this land."))
                }
            }

        })
    }
}