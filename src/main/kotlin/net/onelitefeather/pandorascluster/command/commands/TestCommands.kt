package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandMethod
import net.kyori.adventure.text.minimessage.MiniMessage
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.util.ChunkUtil
import org.bukkit.entity.Player

class TestCommands(private val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("testOwner")
    fun testOwner(player: Player) {
        val land = pandorasClusterApi.getLandService().getFullLand(player.chunk) ?: return
        player.sendMessage("Owner name: " + land.owner?.name)
        player.sendMessage("Owner Id: " + land.owner?.getUniqueId())
    }

    @CommandMethod("chunks")
    fun testChunks(player: Player) {
        val landPlayer = pandorasClusterApi.getLandPlayer(player.uniqueId) ?: return
        val land = pandorasClusterApi.getLandService().getLand(landPlayer) ?: return
        for (placeholder in land.chunks) {
            val chunk = player.world.getChunkAt(
                ChunkUtil.getChunkCoordX(placeholder.chunkIndex),
                ChunkUtil.getChunkCoordZ(placeholder.chunkIndex)
            )
            val x = chunk.x * 16
            val z = chunk.z * 16
            val location = player.world.getHighestBlockAt(x, z).location
            val command = String.format("/tp %s %s %s", location.x + 7.5, location.y + 1, location.z + 7.5)
            player.sendMessage(
                MiniMessage.miniMessage().deserialize(
                    String.format(
                        "<white><click:run_command:%s>%s;%s</click></white>",
                        command,
                        chunk.x,
                        chunk.z
                    )
                )
            )
        }
    }
}