package net.onelitefeather.pandorascluster.service;

import net.onelitefeather.pandorascluster.enums.ChunkRotation;
import net.onelitefeather.pandorascluster.enums.Permission;
import net.onelitefeather.pandorascluster.land.flag.LandFlag;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import java.util.Iterator;

record LandBlockListener(LandService landService) implements Listener {

    @EventHandler
    public void handleBlockExplode(BlockExplodeEvent event) {

        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {

            var next = iterator.next();
            var nextLand = this.landService.getLand(next.getChunk());

            if (nextLand != null) {

                var landFlag = nextLand.getFlag(LandFlag.EXPLOSIONS);
                if (landFlag != null && !landFlag.<Boolean>getValue()) {
                    iterator.remove();
                }
            }
        }
    }

    @EventHandler
    public void handleBlockFromTo(BlockFromToEvent event) {

        var block = event.getBlock();
        var toBlock = event.getToBlock();

        var blockChunk = this.landService.getLand(block.getChunk());
        var toBlockChunk = this.landService.getLand(toBlock.getChunk());

        if (blockChunk != null && toBlockChunk != null && !this.landService.hasSameOwner(blockChunk, toBlockChunk)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleBlockPowered(BlockRedstoneEvent event) {

        var block = event.getBlock();
        var land = this.landService.getLand(block.getChunk());

        if (land != null) {
            var landFlag = land.getFlag(LandFlag.REDSTONE);
            event.setNewCurrent(landFlag != null && landFlag.<Boolean>getValue() ? 0 : event.getOldCurrent());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePistonRetract(BlockPistonRetractEvent event) {

        var block = event.getBlock();
        var location = block.getLocation();
        var blockFace = event.getDirection();

        var land = this.landService.getLand(location.getChunk());

        if (land != null) {
            for (var currentBlock : event.getBlocks()) {

                var location1 = currentBlock.getLocation().add(blockFace.getDirection());
                var currentLand = this.landService.getLand(location1.getChunk());

                if (currentLand != null && !this.landService.hasSameOwner(land, currentLand)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePistonExtend(BlockPistonExtendEvent event) {

        var block = event.getBlock();
        var location = block.getLocation();
        var blockFace = event.getDirection();

        var land = this.landService.getLand(location.getChunk());

        if (land != null) {
            for (var currentBlock : event.getBlocks()) {

                var currentBlockLocation = currentBlock.getLocation().add(blockFace.getDirection());
                var currentLand = this.landService.getLand(currentBlockLocation.getChunk());

                if (currentLand != null && !this.landService.hasSameOwner(land, currentLand)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleBlockBreak(BlockBreakEvent event) {

        var block = event.getBlock();
        var player = event.getPlayer();
        var land = this.landService.getLand(block.getChunk());

        var cancel = land != null && !land.hasAccess(player.getUniqueId());

        if (Permission.BLOCK_BREAK.hasPermission(player)) {
            cancel = false;
        }

        event.setCancelled(cancel);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleBlockPlace(BlockPlaceEvent event) {

        var block = event.getBlock();
        var land = this.landService.getLand(block.getChunk());
        var player = event.getPlayer();

        var cancel = land != null && !land.hasAccess(player.getUniqueId());

        if (Permission.BLOCK_PLACE.hasPermission(player)) {
            cancel = false;
        }

        event.setCancelled(cancel);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleBlockSpread(BlockSpreadEvent event) {

        var source = event.getSource();
        var blockState = event.getNewState();

        var sourceChunk = this.landService.getLand(source.getChunk());
        var blockStateChunk = this.landService.getLand(blockState.getChunk());

        if (sourceChunk != null && blockStateChunk != null) {
            event.setCancelled(!this.landService.hasSameOwner(sourceChunk, blockStateChunk));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleBlockGrow(BlockGrowEvent event) {

        var block = event.getBlock();
        var blockState = event.getNewState();
        var land = this.landService.getLand(block.getChunk());

        if (land != null) {

            var blockFace = ChunkRotation.getBlockFace(block.getLocation());
            var faceLocation = blockState.getLocation().subtract(blockFace.getDirection());
            var blockFaceLand = this.landService.getLand(faceLocation.getChunk());

            if (blockFaceLand != null && !this.landService.hasSameOwner(blockFaceLand, land)) {
                event.setCancelled(true);
            }
        }
    }
}
