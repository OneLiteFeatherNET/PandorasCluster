package net.onelitefeather.pandorascluster.util

import net.onelitefeather.pandorascluster.api.utils.PlayerUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class BukkitPlayerUtil : PlayerUtil {

    override fun getPlayer(uuid: UUID): Player? = Bukkit.getPlayer(uuid)

    override fun isOnline(uuid: UUID): Boolean = getPlayer(uuid) != null

    override fun hasPermission(uuid: UUID, permission: String): Boolean {
        val player = getPlayer(uuid) ?: return false
        return player.hasPermission(permission)
    }
}