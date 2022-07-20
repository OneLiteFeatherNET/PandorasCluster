package net.onelitefeather.pandorascluster.listener;

import net.onelitefeather.pandorascluster.chunk.WorldChunk;
import net.onelitefeather.pandorascluster.service.LandService1;
import net.onelitefeather.pandorascluster.service.LandFlagService;
import net.onelitefeather.pandorascluster.util.Permissions;
import net.onelitefeather.pandorascluster.util.Util;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public record VehicleProtectionListener(LandService1 landService1,
                                        LandFlagService landFlagService) implements Listener {

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {

        Vehicle vehicle = event.getVehicle();
        Entity entered = event.getEntered();
        WorldChunk worldChunk = this.landService1.getWorldChunk(vehicle.getChunk());
        if (worldChunk != null) {
            if (worldChunk.hasAccess(entered.getUniqueId())) return;
            if (this.landFlagService.getBoolean("vehicle-use")) return;
            if (Permissions.VEHICLE_ENTER.hasPermission(entered)) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {

        Vehicle vehicle = event.getVehicle();
        Entity attacker = event.getAttacker();

        WorldChunk worldChunk = this.landService1.getWorldChunk(vehicle.getChunk());

        if (worldChunk != null) {

            if (attacker != null) {
                if (worldChunk.hasAccess(attacker.getUniqueId())) return;
                if (Permissions.VEHICLE_DESTROY.hasPermission(attacker)) return;
                event.setCancelled(true);
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {

        Vehicle vehicle = event.getVehicle();
        Entity attacker = event.getAttacker();

        WorldChunk worldChunk = this.landService1.getWorldChunk(vehicle.getChunk());
        if (worldChunk != null) {
            if (attacker != null) {
                if (worldChunk.hasAccess(attacker.getUniqueId())) return;
                if (Permissions.VEHICLE_DAMAGE.hasPermission(attacker)) return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {

        Vehicle vehicle = event.getVehicle();
        WorldChunk worldChunk = this.landService1.getWorldChunk(vehicle.getChunk());
        if (worldChunk != null) {
            if (this.landFlagService.getBoolean(worldChunk, "vehicle-place")) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {

        Vehicle vehicle = event.getVehicle();

        Location to = event.getTo();
        Location from = event.getFrom();

        Chunk toChunk = to.getChunk();
        Chunk fromChunk = from.getChunk();

        WorldChunk toWorldChunk = this.landService1.getWorldChunk(toChunk);
        WorldChunk fromWorldChunk = this.landService1.getWorldChunk(fromChunk);

        if (toWorldChunk != null) {

            for (Entity entity : vehicle.getPassengers()) {
                if (toWorldChunk.isBanned(entity.getUniqueId())) {
                    vehicle.removePassenger(entity);
                }
            }

            if (fromWorldChunk != null) {
                if (!Util.hasSameOwner(toWorldChunk, fromWorldChunk)) {
                    removeVehicle(vehicle, to);
                }
            }
        } else {
            if (fromWorldChunk != null) {
                removeVehicle(vehicle, to);
            }
        }
    }

    private void removeVehicle(Vehicle vehicle, Location location) {
        List<Entity> passengers = vehicle.getPassengers();

        boolean cancelDrop = false;
        if (!passengers.isEmpty()) {
            for (int i = 0; i < passengers.size() && !cancelDrop; i++) {
                Entity passenger = passengers.get(i);
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
