package net.onelitefeather.pandorascluster.listener;

import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.chunk.WorldChunk;
import net.onelitefeather.pandorascluster.service.LandService1;
import net.onelitefeather.pandorascluster.util.Permissions;
import net.onelitefeather.pandorascluster.util.Util;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class ContainerProtectionListener implements Listener {

    private final LandService1 landService1;

    public ContainerProtectionListener(@NotNull PandorasClusterPlugin pandorasClusterPlugin) {
        this.landService1 = pandorasClusterPlugin.getWorldChunkManager();
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {

        Player player = (Player) event.getPlayer();

        Inventory inventory = event.getInventory();

        if (inventory instanceof DoubleChestInventory doubleChestInventory) {
            DoubleChest doubleChest = doubleChestInventory.getHolder();
            if (doubleChest != null) {

                WorldChunk leftSide = null;
                WorldChunk rightSide = null;

                if (doubleChest.getLeftSide() instanceof BlockState blockState) {
                    leftSide = this.landService1.getWorldChunk(blockState.getChunk());
                }

                if (doubleChest.getRightSide() instanceof BlockState blockState) {
                    rightSide = this.landService1.getWorldChunk(blockState.getChunk());
                }

                if (leftSide != null && rightSide != null) {
                    if (!Util.hasSameOwner(leftSide, rightSide)) {
                        if (leftSide.hasAccess(player.getUniqueId()) && rightSide.hasAccess(player.getUniqueId()))
                            return;
                        if (player.hasPermission(Permissions.OPEN_CONTAINER.getPermission())) return;
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {

        Inventory destination = event.getDestination();
        Inventory source = event.getSource();

        if (source instanceof DoubleChestInventory doubleChestInventory) {
            DoubleChest doubleChest = doubleChestInventory.getHolder();
            if (doubleChest != null) {

                WorldChunk leftSide = null;
                WorldChunk rightSide = null;

                if (doubleChest.getLeftSide() instanceof BlockState blockState) {
                    leftSide = this.landService1.getWorldChunk(blockState.getChunk());
                }

                if (doubleChest.getRightSide() instanceof BlockState blockState) {
                    rightSide = this.landService1.getWorldChunk(blockState.getChunk());
                }

                if (leftSide != null && rightSide != null) {
                    if (!Util.hasSameOwner(leftSide, rightSide)) {
                        event.setCancelled(true);
                    }
                }
            }
        }

        if (source.getHolder() instanceof BlockState sourceBlockState && destination.getHolder() instanceof BlockState destinationBlockState) {

            if (sourceBlockState instanceof Container sourceContainer && destinationBlockState instanceof Container destContainer) {

                WorldChunk sourceChunk = this.landService1.getWorldChunk(sourceContainer.getChunk());
                WorldChunk destChunk = this.landService1.getWorldChunk(destContainer.getChunk());

                if (sourceChunk != null && destChunk != null) {

                    if (sourceContainer instanceof DoubleChestInventory doubleChestInventory) {
                        DoubleChest doubleChest = doubleChestInventory.getHolder();
                        if (doubleChest != null) {

                            Location doubleChestLocation = doubleChest.getLocation();

                            WorldChunk worldChunk = this.landService1.getWorldChunk(doubleChestLocation.getChunk());
                            if (worldChunk != null) {
                                sourceChunk = worldChunk;
                            }
                        }
                    }

                    if (!Util.hasSameOwner(sourceChunk, destChunk)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
