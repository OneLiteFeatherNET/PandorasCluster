package net.onelitefeather.pandorascluster.listener;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.chunk.WorldChunk;
import net.onelitefeather.pandorascluster.service.LandService1;
import net.onelitefeather.pandorascluster.service.LandFlagService;
import net.onelitefeather.pandorascluster.util.Permissions;
import net.onelitefeather.pandorascluster.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class BlockProtectionListener implements Listener {

    private final PandorasClusterPlugin pandorasClusterPlugin;
    private final LandService1 landService1;
    private final LandFlagService landFlagService;

    public BlockProtectionListener(@NotNull PandorasClusterPlugin pandorasClusterPlugin) {
        this.pandorasClusterPlugin = pandorasClusterPlugin;
        this.landService1 = pandorasClusterPlugin.getWorldChunkManager();
        this.landFlagService = pandorasClusterPlugin.getChunkFlagService();
    }

    @EventHandler
    public void handleEntityChangeBlock(EntityChangeBlockEvent event) {

        Block block = event.getBlock();
        WorldChunk worldChunk = this.landService1.getWorldChunk(block.getChunk());
        if (worldChunk != null) {
            if (this.landFlagService.getBoolean(worldChunk, "block-form")) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleEntityBlockForm(EntityBlockFormEvent event) {

        Entity entity = event.getEntity();
        Block block = event.getBlock();

        WorldChunk worldChunk = this.landService1.getWorldChunk(block.getChunk());
        if (worldChunk != null) {
            if (worldChunk.hasAccess(entity.getUniqueId())) return;
            if (this.landFlagService.getBoolean(worldChunk, "block-form")) return;
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {

        WorldChunk worldChunk = this.landService1.getWorldChunk(event.getBlock().getChunk());
        if (worldChunk != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {

        Block block = event.getBlock();
        Block toBlock = event.getToBlock();

        WorldChunk blockChunk = this.landService1.getWorldChunk(block.getChunk());
        WorldChunk toBlockChunk = this.landService1.getWorldChunk(toBlock.getChunk());

        if (blockChunk != null && toBlockChunk != null) {
            if (!Util.hasSameOwner(blockChunk, toBlockChunk)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event) {

        Block block = event.getBlock();
        BlockState blockState = event.getNewState();
        WorldChunk worldChunk = this.landService1.getWorldChunk(block.getChunk());

        if (worldChunk != null) {
            BlockFace blockFace = Arrays.stream(BlockFace.values()).filter(face -> face.getDirection().equals(block.getLocation().getDirection())).findFirst().orElse(null);
            if (blockFace == null) return;
            Location faceLocation = blockState.getLocation().subtract(new Vector(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ()));
            WorldChunk blockFaceChunk = this.landService1.getWorldChunk(faceLocation.getChunk());
            if (blockFaceChunk != null) {
                if (!Util.hasSameOwner(worldChunk, blockFaceChunk)) {
                    event.setCancelled(true);
                }
            }
        } else {
            if (!this.pandorasClusterPlugin.getConfig().getBoolean("settings.allow-block-grow-on-unclaimed-chunk")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {

        List<BlockState> blocks = event.getBlocks();
        if (blocks.isEmpty()) return;

        Location location = blocks.get(0).getLocation();

        WorldChunk area = this.landService1.getWorldChunk(location.getChunk());
        if (area == null) {
            for (int i = blocks.size() - 1; i >= 0; i--) {
                location = blocks.get(i).getLocation();
                if (this.landService1.getWorldChunk(location.getChunk()) == null) {
                    blocks.remove(i);
                }
            }
            return;
        } else {
            WorldChunk origin = this.landService1.getWorldChunk(location.getChunk());
            if (origin == null) {
                event.setCancelled(true);
                return;
            }
            for (int i = blocks.size() - 1; i >= 0; i--) {
                location = blocks.get(i).getLocation();
                if (this.landService1.getWorldChunk(location.getChunk()) == null) {
                    blocks.remove(i);
                    continue;
                }

                WorldChunk plot = this.landService1.getWorldChunk(location.getChunk());
                if (!Objects.equals(plot, origin)) {
                    event.getBlocks().remove(i);
                }
            }
        }

        WorldChunk origin = this.landService1.getWorldChunk(location.getChunk());
        if (origin == null) {
            event.setCancelled(true);
            return;
        }

        for (int i = blocks.size() - 1; i >= 0; i--) {
            location = blocks.get(i).getLocation();
            WorldChunk plot = this.landService1.getWorldChunk(location.getChunk());
            if (!Objects.equals(plot, origin) && (!plot.isMerged() && !origin.isMerged())) {
                event.getBlocks().remove(i);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockRedstone(BlockRedstoneEvent event) {

        Block block = event.getBlock();
        WorldChunk worldChunk = this.landService1.getWorldChunk(block.getChunk());

        if (worldChunk != null) {
            if (!this.landFlagService.getBoolean(worldChunk, "redstone")) {
                event.setNewCurrent(0);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPistonExtend(BlockPistonRetractEvent event) {

        Block block = event.getBlock();
        Location location = block.getLocation();
        BlockFace blockFace = event.getDirection();
        Vector relative = new Vector(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());

        WorldChunk worldChunk = this.landService1.getWorldChunk(location.getChunk());

        if (worldChunk != null) {
            for (Block block1 : event.getBlocks()) {
                Location location1 = block1.getLocation().add(relative);
                WorldChunk chunk = this.landService1.getWorldChunk(location1.getChunk());
                if (chunk != null) {
                    if (!Util.hasSameOwner(worldChunk, chunk)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPistonExtend(BlockPistonExtendEvent event) {

        Block block = event.getBlock();
        Location location = block.getLocation();
        BlockFace blockFace = event.getDirection();
        Vector relative = new Vector(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());

        WorldChunk worldChunk = this.landService1.getWorldChunk(location.getChunk());

        if (worldChunk != null) {
            for (Block block1 : event.getBlocks()) {
                Location location1 = block1.getLocation().add(relative);
                WorldChunk chunk = this.landService1.getWorldChunk(location1.getChunk());
                if (chunk != null) {
                    if (!Util.hasSameOwner(worldChunk, chunk)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        Player player = event.getPlayer();
        WorldChunk worldChunk = this.landService1.getWorldChunk(block.getChunk());

        if (worldChunk != null) if (worldChunk.hasAccess(player.getUniqueId())) return;
        if (Permissions.BUILD_BREAK.hasPermission(player)) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {

        Block block = event.getBlock();
        WorldChunk worldChunk = this.landService1.getWorldChunk(block.getChunk());
        Player player = event.getPlayer();

        if (worldChunk != null) if (worldChunk.hasAccess(player.getUniqueId())) return;
        if (Permissions.BUILD_PLACE.hasPermission(player)) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockSpread(BlockSpreadEvent event) {

        Block source = event.getSource();
        BlockState blockState = event.getNewState();

        WorldChunk sourceChunk = this.landService1.getWorldChunk(source.getChunk());
        WorldChunk blockStateChunk = this.landService1.getWorldChunk(blockState.getChunk());

        if (sourceChunk != null) {
            if (blockStateChunk != null) {
                event.setCancelled(!Util.hasSameOwner(sourceChunk, blockStateChunk));
            }
        }
    }

    @EventHandler
    public void onEntity(HangingBreakEvent event) {

        Entity entity = event.getEntity();

        WorldChunk worldChunk = this.landService1.getWorldChunk(entity.getChunk());
        if (worldChunk == null) return;
        if (this.landFlagService.getBoolean(worldChunk, "hanging-break")) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBigBoom(TNTPrimeEvent event) {

        Block block = event.getBlock();
        WorldChunk worldChunk = this.landService1.getWorldChunk(block.getChunk());
        Entity primerEntity = event.getPrimerEntity();

        if (worldChunk != null) {
            if (primerEntity != null) {
                if (worldChunk.hasAccess(primerEntity.getUniqueId())) return;
                if (Permissions.EXPLOSION_TNT.hasPermission(primerEntity)) return;
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBigBoom(EntityExplodeEvent event) {

        Entity entity = event.getEntity();
        WorldChunk worldChunk = this.landService1.getWorldChunk(entity.getChunk());
        if (worldChunk != null) {

            Iterator<Block> iterator = event.blockList().iterator();
            while (iterator.hasNext()) {
                Block next = iterator.next();
                WorldChunk chunk = this.landService1.getWorldChunk(next.getChunk());
                if (chunk != null) {
                    if (!Util.hasSameOwner(worldChunk, chunk)) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBigBoom(PlayerInteractEvent event) {

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        WorldChunk worldChunk = this.landService1.getWorldChunk(clickedBlock.getChunk());
        if (worldChunk == null) return;

        BlockData blockData = clickedBlock.getBlockData();
        if (blockData instanceof RespawnAnchor respawnAnchor) {

            if (respawnAnchor.getCharges() == respawnAnchor.getMaximumCharges()) {
                boolean explosions = this.landFlagService.getBoolean(worldChunk, "explosions");
                if (!explosions) {
                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                        ItemStack item = event.getItem();
                        event.setCancelled(true);

                        if (!worldChunk.hasAccess(event.getPlayer().getUniqueId())) return;

                        if (item == null) return;
                        Material placeMaterial = item.getType();

                        if (!placeMaterial.isBlock()) return;
                        Block above = clickedBlock.getWorld().getBlockAt(clickedBlock.getRelative(BlockFace.UP).getLocation());
                        above.setType(placeMaterial);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBigBoom(BlockExplodeEvent event) {

        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block next = iterator.next();
            WorldChunk chunk = this.landService1.getWorldChunk(next.getChunk());
            if (chunk != null) {
                boolean explosions = this.landFlagService.getBoolean(chunk, "explosions");
                if (!explosions) {
                    iterator.remove();
                }
            }
        }
    }
}
