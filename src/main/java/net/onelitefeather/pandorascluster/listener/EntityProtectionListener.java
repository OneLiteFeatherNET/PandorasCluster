package net.onelitefeather.pandorascluster.listener;

import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.chunk.WorldChunk;
import net.onelitefeather.pandorascluster.service.LandService1;
import net.onelitefeather.pandorascluster.service.LandFlagService;
import net.onelitefeather.pandorascluster.util.Permissions;
import net.onelitefeather.pandorascluster.util.Util;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityMountEvent;

public class EntityProtectionListener implements Listener {

    private final LandService1 landService1;
    private final LandFlagService landFlagService;

    public EntityProtectionListener(@NotNull PandorasClusterPlugin pandorasClusterPlugin, @NotNull LandFlagService landFlagService) {
        this.landService1 = pandorasClusterPlugin.getWorldChunkManager();
        this.landFlagService = pandorasClusterPlugin.getChunkFlagService();
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {

        Entity mount = event.getMount();
        Entity entity = event.getEntity();

        if (!(mount instanceof Vehicle)) return;

        if (mount instanceof Tameable tameable) {
            if (tameable.isTamed()) {
                AnimalTamer tamer = tameable.getOwner();
                if (tamer != null && tamer.getUniqueId().equals(entity.getUniqueId())) return;
            }
        }

        WorldChunk worldChunk = this.landService1.getWorldChunk(mount.getChunk());
        if (worldChunk == null) return;

        if (worldChunk.hasAccess(entity.getUniqueId())) return;
        if (entity.hasPermission("featherchunks.admin.mount.other")) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        Entity target = event.getEntity();
        Entity attacker = event.getDamager();

        WorldChunk worldChunk = this.landService1.getWorldChunk(target.getChunk());
        if (worldChunk == null) worldChunk = this.landService1.getWorldChunk(attacker.getChunk());

        if (worldChunk == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityPathFind(EntityPathfindEvent event) {
        Entity entity = event.getEntity();

        Location location = event.getLoc();
        WorldChunk worldChunk = this.landService1.getWorldChunk(location.getChunk());
        WorldChunk entityChunk = this.landService1.getWorldChunk(entity.getChunk());

        if (entityChunk != null) {
            if (worldChunk != null) {
                if (!Util.hasSameOwner(entityChunk, worldChunk)) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        } else {
            if (worldChunk != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {

        Entity entity = event.getEntity();
        Entity target = event.getTarget();

        WorldChunk worldChunk = this.landService1.getWorldChunk(entity.getChunk());
        if (target != null) {
            if (worldChunk == null) {
                worldChunk = this.landService1.getWorldChunk(target.getChunk());
            }
        }

        if (worldChunk == null) return;
        event.setCancelled(true);
        // FIXME
//        boolean pve = this.worldChunkManager.getFlagValue(worldChunk, "pve", Boolean.class);
//        if (!pve) {
//
//        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {

        Player player = event.getPlayer();

        Location to = event.getTo();
        Location from = event.getFrom();

        WorldChunk worldChunk = this.landService1.getWorldChunk(to.getChunk());
        if (worldChunk == null) {
            worldChunk = this.landService1.getWorldChunk(from.getChunk());
        }

        if (worldChunk != null) {
            if (Permissions.CHUNK_ENTRY_DENIED.hasPermission(player)) return;
            if (worldChunk.isBanned(player.getUniqueId())) {
                event.setTo(event.getFrom());
            }
        }
    }

    @EventHandler
    public void handleEntityInteract(EntityInteractEvent event) {

        Block block = event.getBlock();

        WorldChunk worldChunk = this.landService1.getWorldChunk(block.getChunk());
        if (worldChunk == null) return;

        BlockData data = block.getBlockData();

        if (data instanceof Farmland) {
            if (!this.landFlagService.getBoolean(worldChunk, "farmland-destroy")) {
                event.setCancelled(true);
            }
        }

        if (data instanceof Powerable) {
            if (!this.landFlagService.getBoolean(worldChunk, "redstone")) {
                event.setCancelled(true);
            }
        }

        if (block.getState() instanceof Container) {
            if (!this.landFlagService.getBoolean(worldChunk, "interact-containers")) {
                event.setCancelled(true);
            }
        }
    }
}
