package net.onelitefeather.pandorascluster.command.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.Confirmation
import com.fastasyncworldedit.core.Fawe
import com.fastasyncworldedit.core.FaweAPI
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.world.RegenOptions
import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import org.bukkit.entity.Player

class UnclaimCommand(private val pandorasClusterApi: PandorasClusterApi) {

    @CommandMethod("land unclaim")
    @CommandDescription("Unclaim your land")
    @Confirmation
    fun execute(player: Player) {

        if (!pandorasClusterApi.hasPlayerLand(player)) {
            player.sendMessage(Component.translatable("no-own-land-found").arguments(pandorasClusterApi.pluginPrefix()))
            return
        }

        val bukkitWorld = player.world
        val world = BukkitAdapter.adapt(bukkitWorld)
        val biome = BukkitAdapter.adapt(bukkitWorld.getBiome(player.location))
        val session = Fawe.instance().worldEdit.newEditSession(world)

        val land = pandorasClusterApi.getLand(player.chunk) ?: return
        land.chunks.forEach { chunkPlaceholder ->
            val bukkitChunk = player.world.getChunkAt(chunkPlaceholder.chunkIndex)
            val posX = (bukkitChunk.x shl 4).toDouble()
            val posZ = (bukkitChunk.z shl 4).toDouble()

            val pos1 = BlockVector3.at(posX, bukkitWorld.minHeight.toDouble(), posZ)
            val pos2 = BlockVector3.at(posX + 15, bukkitWorld.maxHeight.toDouble(), posZ + 15)

            val cuboidRegion = CuboidRegion(world, pos1, pos2)
            val regenOptions =
                RegenOptions.builder().regenBiomes(true).biomeType(biome).seed(bukkitWorld.seed).build()

            FaweAPI.getTaskManager().async {
                world.regenerate(cuboidRegion, session, regenOptions)
                session.close()
            }
        }

        player.sendMessage(Component.translatable("command.unclaim.success").arguments(pandorasClusterApi.pluginPrefix()))
        pandorasClusterApi.unclaimLand(player)
    }
}