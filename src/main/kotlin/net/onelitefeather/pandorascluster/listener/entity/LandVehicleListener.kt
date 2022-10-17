package net.onelitefeather.pandorascluster.listener.entity;

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.vehicle.*

class LandVehicleListener(val pandorasClusterApi: PandorasClusterApi) : Listener {

    @EventHandler
    fun handleVehicleDestroy(event: VehicleDestroyEvent) {
        val vehicle = event.vehicle
        val attacker = event.attacker
        val land = pandorasClusterApi.getLand(vehicle.chunk)
        if (land != null) {
            if (land.getLandFlag(LandFlag.VEHICLE_DAMAGE).getValue<Boolean>() == true) return
            if (attacker != null) {
                if (land.hasAccess(attacker.uniqueId)) return
                if (Permission.VEHICLE_DESTROY.hasPermission(attacker)) return
                event.isCancelled = true
                return
            }
            event.isCancelled = true
        }
    }

    @EventHandler
    fun handleVehicleDamage(event: VehicleDamageEvent) {
        val vehicle = event.vehicle
        val attacker = event.attacker
        val land = pandorasClusterApi.getLand(vehicle.chunk)
        if (land != null) {
            if (attacker != null) {
                if (land.hasAccess(attacker.uniqueId)) return
                if (Permission.VEHICLE_DAMAGE.hasPermission(attacker)) return
            }
            if (land.getLandFlag(LandFlag.VEHICLE_DAMAGE).getValue<Boolean>() == true) return
            event.isCancelled = true
        }
    }

    @EventHandler
    fun handleVehicleCreation(event: VehicleCreateEvent) {
        val vehicle = event.vehicle
        val land = pandorasClusterApi.getLand(vehicle.chunk) ?: return
        event.isCancelled = land.getLandFlag(LandFlag.VEHICLE_CREATE).getValue<Boolean>() == false
    }

    @EventHandler
    fun handleVehicleMovement(event: VehicleMoveEvent) {
        val vehicle = event.vehicle
        val to = event.to
        val toLand = pandorasClusterApi.getLand(to.chunk)
        if (toLand != null) {
            vehicle.passengers.filter { toLand.isBanned(it.uniqueId) }.forEach { vehicle.removePassenger(it) }
        }
    }

    @EventHandler
    fun handleVehicleEnter(event: VehicleEnterEvent) {
        val vehicle = event.vehicle
        val entered = event.entered
        val land = pandorasClusterApi.getLand(vehicle.chunk)
        if (land != null) {
            if (land.getLandFlag(LandFlag.VEHICLE_USE).getValue<Boolean>() == true) return
            if (land.hasAccess(entered.uniqueId)) return
            if (Permission.VEHICLE_ENTER.hasPermission(entered)) return
            event.isCancelled = true
        }
    }
}
