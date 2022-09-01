package net.onelitefeather.pandorascluster.listener

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.util.hasSameOwner
import org.bukkit.block.BlockState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.inventory.DoubleChestInventory

class LandContainerProtectionListener(val pandorasClusterApi: PandorasClusterApi) : Listener {

    @EventHandler
    fun handleInventoryMoveItem(event: InventoryMoveItemEvent) {

        val destinationInventory = event.destination
        if (destinationInventory is DoubleChestInventory) {

            val doubleChest = destinationInventory.holder ?: return
            val sideLeft = doubleChest.leftSide
            val sideRight = doubleChest.rightSide

            val cancel = if (sideLeft is BlockState || sideRight is BlockState) {
                if (sideLeft == null || sideRight == null) {
                    true
                } else {
                    val land = pandorasClusterApi.getLand((sideLeft as BlockState).chunk)
                    val otherLand = pandorasClusterApi.getLand((sideRight as BlockState).chunk)
                    land == null || otherLand == null || !hasSameOwner(land, otherLand)
                }
            } else {
                null
            }

            event.isCancelled = cancel == true
            return
        }

        if (destinationInventory.holder is BlockState) {
            val land = pandorasClusterApi.getLand((destinationInventory.holder as BlockState).block.chunk)
            val sourceLand = pandorasClusterApi.getLand((event.source.holder as BlockState).block.chunk)
            event.isCancelled = land == null || sourceLand == null || !hasSameOwner(sourceLand, land)
        }
    }
}