package net.onelitefeather.pandorascluster.service;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import net.onelitefeather.pandorascluster.enums.Permission;
import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.land.flag.LandFlag;
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity;
import net.onelitefeather.pandorascluster.service.services.LandFlagService;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.Iterator;
import java.util.List;

record LandEntityListener(LandService landService, LandFlagService landFlagService) implements Listener {

    @EventHandler
    public void theConfuser(EntityDamageByEntityEvent event) {

        var target = event.getEntity();
        var attacker = event.getDamager();

        var land = this.landService.getLand(target.getChunk());

        if (land == null) land = this.landService.getLand(attacker.getChunk());

        if (land == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void handleEntityExplode(TNTPrimeEvent event) {

        var block = event.getBlock();
        var land = this.landService.getLand(block.getChunk());
        var primerEntity = event.getPrimerEntity();

        if (land != null) {

            LandFlagEntity landFlag = this.landFlagService.getFlag(LandFlag.EXPLOSIONS, land);
            var cancel = landFlag != null && !landFlag.<Boolean>getValue();

            if (primerEntity != null) {
                if (land.hasAccess(primerEntity.getUniqueId())) return;
                if (Permission.EXPLOSION.hasPermission(primerEntity)) return;
                cancel = true;
            }

            event.setCancelled(cancel);
        }
    }

    @EventHandler
    public void handleEntityExplode(EntityExplodeEvent event) {

        var entity = event.getEntity();
        var land = this.landService.getLand(entity.getChunk());

        if (land != null) {

            Iterator<Block> iterator = event.blockList().iterator();
            while (iterator.hasNext()) {

                var next = iterator.next();
                var nextLand = this.landService.getLand(next.getChunk());

                if (nextLand != null && !this.landService.hasSameOwner(land, nextLand)) {
                    iterator.remove();
                }
            }
        }
    }

    @EventHandler
    public void handleEntityMount(EntityMountEvent event) {

        var mount = event.getMount();
        var entity = event.getEntity();

        if (!(mount instanceof Vehicle)) return;

        if (mount instanceof Tameable tameable) {
            var tamer = tameable.getOwner();
            if (tameable.isTamed() && tamer != null && tamer.getUniqueId().equals(entity.getUniqueId())) return;
        }

        var land = this.landService.getLand(mount.getChunk());
        if (land == null) return;

        if (land.hasAccess(entity.getUniqueId())) return;
        if (Permission.ENTITY_MOUNT.hasPermission(entity)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void handleEntityChangeBlock(EntityChangeBlockEvent event) {
        var land = this.landService.getLand(event.getBlock().getChunk());

        if (land == null) return;

        var landFlag = this.landFlagService.getFlag(LandFlag.BLOCK_FORM, land);
        if (landFlag != null && landFlag.<Boolean>getValue()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void handleEntityBlockForm(EntityBlockFormEvent event) {

        var land = this.landService.getLand(event.getBlock().getChunk());
        if (land == null) return;

        var landFlag = this.landFlagService.getFlag(LandFlag.BLOCK_FORM, land);
        if (landFlag != null) {
            if (land.hasAccess(event.getEntity().getUniqueId())) return;

            boolean flag = landFlag.getValue();
            if (flag) return;

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleHangingBreak(HangingBreakEvent event) {

        var entity = event.getEntity();

        var land = this.landService.getLand(entity.getChunk());
        if (land == null) return;

        var landFlag = this.landFlagService.getFlag(LandFlag.HANGING_BREAK, land);
        if (landFlag != null && landFlag.<Boolean>getValue()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void handleEntityInteract(EntityInteractEvent event) {

        var entity = event.getEntity();
        var block = event.getBlock();

        var land = this.landService.getLand(block.getChunk());
        if (land == null) return;

        var data = block.getBlockData();

        var farmLandDestroyFlag = this.landFlagService.getFlag(LandFlag.FARMLAND_DESTROY, land);
        var redstoneFlag = this.landFlagService.getFlag(LandFlag.REDSTONE, land);

        if (data instanceof Farmland && farmLandDestroyFlag != null && !farmLandDestroyFlag.<Boolean>getValue() ||
                data instanceof Powerable && redstoneFlag != null && !redstoneFlag.<Boolean>getValue()) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void handleEntityPathfinding(EntityPathfindEvent event) {

        var entity = event.getEntity();

        var location = event.getLoc();
        var land = this.landService.getLand(location.getChunk());
        var entityLand = this.landService.getLand(entity.getChunk());

        var cancel = false;

        if (entityLand != null) {
            if (land != null) {
                if (!this.landService.hasSameOwner(entityLand, land)) {
                    cancel = true;
                }
            } else {
                cancel = true;
            }
        } else {
            if (land != null) {
                cancel = true;
            }
        }

        event.setCancelled(cancel);
    }

    @EventHandler
    public void handleEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {

        var entity = event.getEntity();
        var target = event.getTarget();

        var land = this.landService.getLand(entity.getChunk());
        if (target != null && land == null) {
            land = this.landService.getLand(target.getChunk());
        }

        if (land == null) return;

        var landFlag = this.landFlagService.getFlag(LandFlag.PVE, land);
        if (landFlag != null && landFlag.<Boolean>getValue()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void handleVehicleEnter(VehicleEnterEvent event) {

        var vehicle = event.getVehicle();
        var entered = event.getEntered();

        var land = this.landService.getLand(vehicle.getChunk());

        if (land != null) {

            LandFlagEntity landFlag = this.landFlagService.getFlag(LandFlag.VEHICLE_USE, land);
            if (landFlag != null && landFlag.<Boolean>getValue()) return;

            if (land.hasAccess(entered.getUniqueId())) return;

            if (Permission.VEHICLE_ENTER.hasPermission(entered)) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleVehicleDestroy(VehicleDestroyEvent event) {

        var vehicle = event.getVehicle();
        var attacker = event.getAttacker();

        var land = this.landService.getLand(vehicle.getChunk());

        if (land != null) {

            var landFlag = this.landFlagService.getFlag(LandFlag.VEHICLE_DAMAGE, land);
            if (landFlag != null && landFlag.<Boolean>getValue()) return;

            if (attacker != null) {
                if (land.hasAccess(attacker.getUniqueId())) return;
                if (Permission.VEHICLE_DESTROY.hasPermission(attacker)) return;
                event.setCancelled(true);
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleVehicleDamage(VehicleDamageEvent event) {

        var vehicle = event.getVehicle();
        var attacker = event.getAttacker();

        var land = this.landService.getLand(vehicle.getChunk());
        if (land != null) {

            if (attacker != null) {
                if (land.hasAccess(attacker.getUniqueId())) return;
                if (Permission.VEHICLE_DAMAGE.hasPermission(attacker)) return;
            }

            var landFlag = this.landFlagService.getFlag(LandFlag.VEHICLE_DAMAGE, land);
            if (landFlag != null && landFlag.<Boolean>getValue()) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleVehicleCreation(VehicleCreateEvent event) {

        var vehicle = event.getVehicle();
        var land = this.landService.getLand(vehicle.getChunk());
        if (land != null) {
            var landFlag = this.landFlagService.getFlag(LandFlag.VEHICLE_CREATE, land);
            if (landFlag != null && landFlag.<Boolean>getValue()) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleVehicleMovement(VehicleMoveEvent event) {

        var vehicle = event.getVehicle();

        var to = event.getTo();
        var from = event.getFrom();

        var toLand = this.landService.getLand(to.getChunk());
        var fromLand = this.landService.getLand(from.getChunk());

        if (toLand != null) {
            for (var entity : vehicle.getPassengers()) {
                if (toLand.isBanned(entity.getUniqueId())) {
                    vehicle.removePassenger(entity);
                }
            }
        }

        if (fromLand != null) {
            removeVehicle(vehicle, toLand != null && !this.landService.hasSameOwner(toLand, fromLand) ? to : from);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePotionSplash(ProjectileLaunchEvent event) {

        var projectile = event.getEntity();

        if (projectile instanceof ThrownPotion thrownPotion) {

            var source = thrownPotion.getShooter();

            if (source instanceof Entity entity) {
                var land = this.landService.getLand(entity.getChunk());

                if (land != null) {

                    var landFlag = this.landFlagService.getFlag(LandFlag.POTION_SPLASH, land);
                    if (landFlag != null && landFlag.<Boolean>getValue()) return;

                    if (land.hasAccess(entity.getUniqueId())) return;
                    if (Permission.POTION_SPLASH.hasPermission(entity)) return;

                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void handleProjectileHit(ProjectileHitEvent event) {

        var entity = event.getEntity();
        ProjectileSource shooter = null;

        if (entity.getShooter() instanceof Entity) {
            shooter = entity.getShooter();
        }

        var hitEntity = event.getHitEntity();
        var hitBlock = event.getHitBlock();

        Land land = null;

        if (hitEntity != null) {
            land = this.landService.getLand(hitEntity.getChunk());
        }

        if (hitBlock != null) {
            land = this.landService.getLand(hitBlock.getChunk());
        }

        if (land != null) {

            var cancel = false;

            if (shooter instanceof Permissible permissible) {
                cancel = !Permission.PROJECTILE_HIT_ENTITY.hasPermission(permissible);
            }


            if (hitBlock != null) {
                var blockData = hitBlock.getBlockData();
                if (blockData instanceof Powerable) {
                    var landFlag = this.landFlagService.getFlag(LandFlag.REDSTONE, land);
                    cancel = landFlag != null && !landFlag.<Boolean>getValue();
                }
            }

            event.setCancelled(cancel);
        }
    }

    private void removeVehicle(@NotNull Vehicle vehicle, @NotNull Location location) {
        List<Entity> passengers = vehicle.getPassengers();

        boolean cancelDrop = false;
        if (!passengers.isEmpty()) {
            for (int i = 0; i < passengers.size() && !cancelDrop; i++) {
                var passenger = passengers.get(i);
                if (passenger instanceof Player player && player.getGameMode() == GameMode.CREATIVE) {
                    cancelDrop = true;
                }
            }
        }

        if (!cancelDrop) {

            ItemStack dropItem = null;
            if (vehicle instanceof Minecart minecart) {
                dropItem = new ItemStack(minecart.getMinecartMaterial());
            } else if (vehicle instanceof Boat boat) {
                dropItem = new ItemStack(boat.getBoatType().getMaterial());
            }

            if (dropItem != null) {
                location.getWorld().dropItemNaturally(location, dropItem);
            }
        }

        vehicle.remove();
    }

}
