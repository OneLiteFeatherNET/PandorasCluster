package net.onelitefeather.pandorascluster.listener;

import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.chunk.WorldChunk;
import net.onelitefeather.pandorascluster.service.LandService1;
import net.onelitefeather.pandorascluster.service.LandFlagService;
import net.onelitefeather.pandorascluster.util.Permissions;
import org.bukkit.block.Block;
import org.bukkit.block.Campfire;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

public class ProjectileProtectionListener implements Listener {

    private final PandorasClusterPlugin pandorasClusterPlugin;
    private final LandService1 landService1;
    private final LandFlagService landFlagService;

    public ProjectileProtectionListener(@NotNull PandorasClusterPlugin pandorasClusterPlugin) {
        this.pandorasClusterPlugin = pandorasClusterPlugin;
        this.landService1 = pandorasClusterPlugin.getWorldChunkManager();
        this.landFlagService = pandorasClusterPlugin.getChunkFlagService();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPotionSplashEvent(ProjectileLaunchEvent event) {

        Projectile projectile = event.getEntity();
        if (projectile instanceof ThrownPotion thrownPotion) {
            ProjectileSource source = thrownPotion.getShooter();
            if (source != null) {
                if (source instanceof Entity entity) {

                    WorldChunk worldChunk = this.landService1.getWorldChunk(entity.getChunk());
                    if (worldChunk != null) {

                        if (worldChunk.hasAccess(entity.getUniqueId())) return;
                        if (this.landFlagService.getBoolean(worldChunk, "potion-splash")) return;
                        if (Permissions.PROJECTILE_LAUNCH.hasPermission(entity)) return;

                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {

        Projectile entity = event.getEntity();
        ProjectileSource shooter = null;

        if (entity.getShooter() instanceof Entity) {
            shooter = entity.getShooter();
        }

        Entity hitEntity = event.getHitEntity();
        if (hitEntity != null) {
            WorldChunk worldChunk = this.landService1.getWorldChunk(hitEntity.getChunk());

            if (shooter != null) {
                if (shooter instanceof Entity shooterEntity) {
                    if (worldChunk.hasAccess(shooterEntity.getUniqueId())) return;
                    if (Permissions.PROJECTILE_HIT_ENTITY.hasPermission(shooterEntity)) return;
                    event.setCancelled(true);
                }
            }
        }

        Block hitBlock = event.getHitBlock();
        if (hitBlock != null) {

            WorldChunk worldChunk = this.landService1.getWorldChunk(hitBlock.getChunk());

            if (worldChunk != null) {

                boolean cancel = hitBlock.getState() instanceof Campfire;

                BlockData blockData = hitBlock.getBlockData();
                if (blockData instanceof Powerable) {
                    if (this.landFlagService.getBoolean(worldChunk, "redstone")) return;
                    cancel = true;
                }

                event.setCancelled(cancel);
            }
        }
    }
}
