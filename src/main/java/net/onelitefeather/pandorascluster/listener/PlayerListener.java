package net.onelitefeather.pandorascluster.listener;

import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.chunk.WorldChunk;
import net.onelitefeather.pandorascluster.service.LandService1;
import net.onelitefeather.pandorascluster.service.LandFlagService;
import net.onelitefeather.pandorascluster.util.Permissions;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {

    private final LandService1 landService1;
    private final LandFlagService landFlagService;

    public PlayerListener(PandorasClusterPlugin pandorasClusterPlugin) {
        this.landService1 = pandorasClusterPlugin.getWorldChunkManager();
        this.landFlagService = pandorasClusterPlugin.getChunkFlagService();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block != null) {

            WorldChunk worldChunk = this.landService1.getWorldChunk(block.getChunk());
            if (worldChunk == null) return;

            BlockData data = block.getBlockData();
            if (data instanceof Farmland) {
                if (event.getAction() == Action.PHYSICAL) {
                    if (worldChunk.hasAccess(player.getUniqueId())) return;
                    if (!this.landFlagService.getBoolean(worldChunk, "farmland-destroy")) {
                        event.setCancelled(true);
                        event.setUseInteractedBlock(Event.Result.DENY);
                    }
                }
            }

            if (data instanceof Powerable) {
                if (worldChunk.hasAccess(player.getUniqueId())) return;
                if (player.hasPermission("featherchunks.admin.redstone.other")) return;
                event.setCancelled(true);
            }

            if (block.getState() instanceof Container) {
                if (worldChunk.hasAccess(player.getUniqueId())) return;
                if (player.hasPermission("featherchunks.admin.interact.container")) return;
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        if (!event.hasExplicitlyChangedBlock()) return;
        Chunk to = event.getTo().getChunk();

        WorldChunk toChunk = this.landService1.getWorldChunk(to);
        if (toChunk != null) {
            if (Permissions.CHUNK_ENTRY_DENIED.hasPermission(player)) return;
            if (!toChunk.isBanned(player.getUniqueId())) return;
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawnLocation(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        WorldChunk worldChunk = this.landService1.getWorldChunk(event.getRespawnLocation().getChunk());
        if (worldChunk != null) {
            if (Permissions.CHUNK_ENTRY_DENIED.hasPermission(player)) return;
            if (worldChunk.isBanned(player.getUniqueId())) {
                event.setRespawnLocation(player.getWorld().getSpawnLocation());
            }
        }
    }
}
