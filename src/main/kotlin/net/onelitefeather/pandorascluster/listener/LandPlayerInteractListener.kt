package net.onelitefeather.pandorascluster.listener

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class LandPlayerInteractListener(val pandorasClusterApi: PandorasClusterApi) : Listener {

    @EventHandler
    fun handlePlayerInteractEntity(event: PlayerInteractEntityEvent) {

        val player = event.player
        val entity = event.rightClicked
        val land = this.pandorasClusterApi.getLand(entity.chunk) ?: return

        if(player.hasPermission(Permission.ENTITY_INTERACT)) return
        if(land.hasAccess(player.uniqueId)) return
        if(land.getLandFlag(LandFlag.ENTITY_INTERACT)?.getValue<Boolean>() == true) return
        event.isCancelled = true
    }
}