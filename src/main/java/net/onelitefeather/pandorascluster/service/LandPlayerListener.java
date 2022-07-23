package net.onelitefeather.pandorascluster.service;

import net.onelitefeather.pandorascluster.enums.Permission;
import net.onelitefeather.pandorascluster.land.flag.LandFlag;
import org.bukkit.block.Container;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public record LandPlayerListener(LandService landService) implements Listener {

    @EventHandler
    public void handlePlayerMovement(PlayerMoveEvent event) {

        var player = event.getPlayer();

        if (!event.hasExplicitlyChangedBlock()) return;

        var toLand = this.landService.getLand(event.getTo().getChunk());

        if (toLand != null) {
            if (Permission.LAND_ENTRY_DENIED.hasPermission(player)) return;
            if (!toLand.isBanned(player.getUniqueId())) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent event) {

        var clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        var player = event.getPlayer();

        var land = this.landService.getLand(clickedBlock.getChunk());
        if (land == null) return;

        var blockData = clickedBlock.getBlockData();
        boolean cancel = false;

        if (blockData instanceof Farmland && event.getAction() == Action.PHYSICAL) {
            var landFlag = land.getFlag(LandFlag.FARMLAND_DESTROY);
            if (landFlag != null && landFlag.<Boolean>getValue()) return;

            if (land.hasAccess(player.getUniqueId())) return;
            if (Permission.INTERACT_FARMLAND.hasPermission(player)) return;
            cancel = true;
        }

        if (blockData instanceof Powerable) {

            var landFlag= land.getFlag(LandFlag.REDSTONE);
            if (landFlag != null && landFlag.<Boolean>getValue()) return;

            if (land.hasAccess(player.getUniqueId())) return;
            if (Permission.USE_REDSTONE.hasPermission(player)) return;
            cancel = true;
        }

        if (clickedBlock.getState() instanceof Container) {
            if (land.hasAccess(player.getUniqueId())) return;
            if (Permission.INTERACT_CONTAINERS.hasPermission(player)) return;
            cancel = true;
        }

        if (blockData instanceof RespawnAnchor respawnAnchor &&
                respawnAnchor.getCharges() == respawnAnchor.getMaximumCharges() &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            
            var landFlag = land.getFlag(LandFlag.EXPLOSIONS);
            if (landFlag != null && landFlag.<Boolean>getValue()) return;
            if (Permission.EXPLOSION.hasPermission(player)) return;
            cancel = true;
        }

        event.setCancelled(cancel);
    }

    @EventHandler
    public void handlePlayerTeleport(PlayerTeleportEvent event) {

        Player player = event.getPlayer();

        var to = event.getTo();
        var from = event.getFrom();

        var land = this.landService.getLand(to.getChunk());
        if (land == null) {
            land = this.landService.getLand(from.getChunk());
        }

        if (land != null) {
            if (Permission.LAND_ENTRY_DENIED.hasPermission(player)) return;
            if (land.isBanned(player.getUniqueId())) {
                event.setTo(event.getFrom());
            }
        }
    }
}
