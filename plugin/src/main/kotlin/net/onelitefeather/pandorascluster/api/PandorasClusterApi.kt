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
import java.util.logging.Logger

interface PandorasClusterApi {

    fun pluginPrefix(): Component

    fun getPlugin(): PandorasClusterPlugin

    fun getDatabaseStorageService(): DatabaseStorageService

    /**
     * @param player the player
     * @return true if the player owns a land.
     */
    fun hasPlayerLand(player: Player): Boolean

    /**
     * @param playerId the uuid of the player
     * @return true if the player owns a land.
     */
    fun hasPlayerLand(playerId: UUID): Boolean

    /**
     * @param chunk the chunk
     * @return true if the chunk is claimed.
     */
    fun isChunkClaimed(chunk: Chunk): Boolean

    /**
     * @param land the land to unclaim
     */
    fun unclaimLand(land: Land)

    /**
     * @return A list of all [Land]'s
     */
    fun getLands(): List<Land>

    /**
     * @param player the player
     * @return the landplayer by the bukkit player
     */
    fun getLandPlayer(player: Player): LandPlayer?

    /**
     * @param uuid the uuid of the player
     * @return the landplayer by the players uuid
     */
    fun getLandPlayer(uuid: UUID): LandPlayer?

    /**
     * @param name the players name
     * @return the landplayer by the players name
     */
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

    /**
     * @param chunk the chunk
     * @return the land by the chunk.
     */
    fun getLand(chunk: Chunk): Land?

    /**
     * @param landOwner the owner of the land
     * @return the land by the land owner.
     */
    fun getLand(landOwner: LandPlayer): Land?

    /**
     * @param uuid the player uuid
     * @param name the player name
     * @return true if the registration was successfully else the user is already registred.
     */
    fun registerPlayer(uuid: UUID, name: String): Boolean

    fun getStaffNotificaton(): StaffNotificationService
}