package net.onelitefeather.pandorascluster.listener.entity;

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.vehicle.VehicleCreateEvent
import org.bukkit.event.vehicle.VehicleDamageEvent
import org.bukkit.event.vehicle.VehicleDestroyEvent
import org.bukkit.event.vehicle.VehicleMoveEvent

class LandVehicleListener(val pandorasClusterApi: PandorasClusterApi) : Listener, EntityUtils, ChunkUtils {

    @EventHandler
    fun handleVehicleDestroy(event: VehicleDestroyEvent) {

        val vehicle = event.vehicle
        val attacker = event.attacker
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(vehicle.chunk))
        if (land != null) {
            if (land.getFlag(LandFlag.VEHICLE_DAMAGE).getValue<Boolean>() == true) return
            if (attacker != null) {
                if (land.hasMemberAccess(attacker.uniqueId)) return
                if (hasPermission(attacker, LandFlag.VEHICLE_DAMAGE)) return
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
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(vehicle.chunk))

        if (land != null) {
            if (attacker != null) {
                if (land.hasMemberAccess(attacker.uniqueId)) return
                if (hasPermission(attacker, LandFlag.VEHICLE_DAMAGE)) return
            }
            if (land.getFlag(LandFlag.VEHICLE_DAMAGE).getValue<Boolean>() == true) return
            event.isCancelled = true
        }
    }

    @EventHandler
    fun handleVehicleCreation(event: VehicleCreateEvent) {
        val vehicle = event.vehicle
        if (vehicle.entitySpawnReason == CreatureSpawnEvent.SpawnReason.DEFAULT) return
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(vehicle.chunk)) ?: return
        event.isCancelled = land.getFlag(LandFlag.VEHICLE_CREATE).getValue<Boolean>() == false
    }

    @EventHandler
    fun handleVehicleMovement(event: VehicleMoveEvent) {
        val vehicle = event.vehicle
        val to = event.to
        val toLand = pandorasClusterApi.getLandService().getLand(toClaimedChunk(to.chunk))
        if (toLand != null) {
            vehicle.passengers.filter { toLand.isBanned(it.uniqueId) }.forEach { vehicle.removePassenger(it) }
        }
    }
}
