package net.onelitefeather.pandorascluster.api

import net.kyori.adventure.text.Component
import net.onelitefeather.pandorascluster.PandorasClusterPlugin
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.service.*
import org.bukkit.Chunk
import org.bukkit.entity.Player
import org.hibernate.SessionFactory
import java.util.*
import java.util.function.Consumer
import java.util.logging.Logger

interface PandorasClusterApi {

    fun getPlugin(): PandorasClusterPlugin

    fun getDatabaseStorageService(): DatabaseStorageService

    fun hasPlayerLand(player: Player): Boolean

    fun hasPlayerLand(playerId: UUID): Boolean

    fun isChunkClaimed(chunk: Chunk): Boolean

    /**
     * @return A list of all [Land]'s
     */
    fun getLands(): List<Land>

    fun getLandPlayer(player: Player): LandPlayer?

    fun getLandPlayer(uuid: UUID): LandPlayer?

    fun getLandPlayer(name: String): LandPlayer?

    /**
     * @param player the player
     * @return A list of all [Land]'s where the player has access.
     */
    fun getLands(player: Player): List<Land>

    fun getSessionFactory(): SessionFactory

    fun getLandPlayerService(): LandPlayerService

    fun getDatabaseService(): DatabaseService

    fun getLandService(): LandService

    fun getLogger(): Logger

    fun translateLegacyCodes(text: String): Component

    fun getLand(chunk: Chunk): Land?

    fun registerPlayer(uuid: UUID, name: String, consumer: Consumer<Boolean>)
}
