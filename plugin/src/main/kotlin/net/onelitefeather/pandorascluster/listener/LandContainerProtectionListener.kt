package net.onelitefeather.pandorascluster.listener

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import org.bukkit.block.BlockState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.inventory.DoubleChestInventory

class LandContainerProtectionListener(val pandorasClusterApi: PandorasClusterApi) : Listener, ChunkUtils {

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

                    val leftChunk = toClaimedChunk((sideLeft as BlockState).chunk)
                    val rightChunk = toClaimedChunk((sideRight as BlockState).chunk)

                    val land = pandorasClusterApi.getLandService().getLand(leftChunk)
                    val otherLand = pandorasClusterApi.getLandService().getLand(rightChunk)
                    land == null || otherLand == null || !hasSameOwner(land, otherLand)
                }
            } else {
                null
            }

            event.isCancelled = cancel == true
            return
        }

        if (destinationInventory.holder is BlockState) {

            val destinationChunk = toClaimedChunk((destinationInventory.holder as BlockState).block.chunk)
            val holder = toClaimedChunk((event.source.holder as BlockState).block.chunk)

            val land = pandorasClusterApi.getLandService().getLand(destinationChunk)
            val sourceLand = pandorasClusterApi.getLandService().getLand(holder)
            event.isCancelled = land == null || sourceLand == null || !hasSameOwner(sourceLand, land)
        }
    }
}