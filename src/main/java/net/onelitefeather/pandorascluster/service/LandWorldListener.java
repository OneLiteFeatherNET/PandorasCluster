package net.onelitefeather.pandorascluster.service;

import net.onelitefeather.pandorascluster.land.flag.LandFlag;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.List;
import java.util.Objects;

record LandWorldListener(LandService landService) implements Listener {

    @EventHandler
    public void handleLeavesDecay(LeavesDecayEvent event) {

        var land = this.landService.getLand(event.getBlock().getChunk());
        if (land == null) return;

        var landFlag = land.getFlag(LandFlag.LEAVES_DECAY);
        if(landFlag != null && landFlag.<Boolean>getValue()) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleStructureGrow(StructureGrowEvent event) {

        List<BlockState> blocks = event.getBlocks();
        if (blocks.isEmpty()) return;

        Location location = blocks.get(0).getLocation();

        var area = this.landService.getLand(location.getChunk());
        if (area == null) {
            for (int i = blocks.size() - 1; i >= 0; i--) {
                location = blocks.get(i).getLocation();
                if (this.landService.getLand(location.getChunk()) == null) {
                    blocks.remove(i);
                }
            }
            return;
        } else {
            var origin = this.landService.getLand(location.getChunk());
            if (origin == null) {
                event.setCancelled(true);
                return;
            }
            for (int i = blocks.size() - 1; i >= 0; i--) {
                location = blocks.get(i).getLocation();
                if (this.landService.getLand(location.getChunk()) == null) {
                    blocks.remove(i);
                    continue;
                }

                var plot = this.landService.getLand(location.getChunk());
                if (!Objects.equals(plot, origin)) {
                    event.getBlocks().remove(i);
                }
            }
        }

        var origin = this.landService.getLand(location.getChunk());
        if (origin == null) {
            event.setCancelled(true);
            return;
        }

        for (int i = blocks.size() - 1; i >= 0; i--) {
            location = blocks.get(i).getLocation();
            var land = this.landService.getLand(location.getChunk());
            if (land != null && !Objects.equals(land, origin) && (!land.isMerged() && !origin.isMerged())) {
                event.getBlocks().remove(i);
            }
        }
    }
}
