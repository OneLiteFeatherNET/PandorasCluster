package net.onelitefeather.pandorascluster.listener

import com.destroystokyo.paper.event.block.TNTPrimeEvent
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import org.bukkit.block.data.type.Farmland
import org.bukkit.block.data.type.TurtleEgg
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.event.vehicle.*
import org.bukkit.permissions.Permissible
import org.spigotmc.event.entity.EntityMountEvent

class LandEntityListener(private val pandorasClusterApi: PandorasClusterApi) :
    Listener {

    @EventHandler
    fun handleProjectileBlockHit(event: ProjectileHitEvent) {
        val entity = event.entity
        val shooter = if (entity.shooter is Entity) {
            entity.shooter as Entity
        } else null ?: return
        val hitBlock = event.hitBlock
        if (hitBlock != null) {
            val land = pandorasClusterApi.getLand(hitBlock.chunk) ?: return
            event.isCancelled = !land.hasAccess(shooter.uniqueId)
        }
    }

    @EventHandler
    fun theConfuser(event: EntityDamageByEntityEvent) {

        val target = event.entity
        var attacker = event.damager

        if (attacker is Projectile) {
            attacker = attacker.shooter as Entity
        }

        val land = pandorasClusterApi.getLand(target.chunk) ?: pandorasClusterApi.getLand(attacker.chunk) ?: return

        event.isCancelled = if (target is Player && attacker is Player) {
            val flag = pandorasClusterApi.getLandFlag(LandFlag.PVP, land) ?: return
            val value = flag.getValue<Boolean>() == true
            if (attacker.hasPermission(Permission.PVP)) return
            if (land.hasAccess(attacker.uniqueId) && land.hasAccess(target.uniqueId)) return
            !value
        } else {
            val flag = pandorasClusterApi.getLandFlag(LandFlag.PVE, land) ?: return
            val value = flag.getValue<Boolean>() == true
            if (target.hasPermission(Permission.PVE) || attacker.hasPermission(Permission.PVE)) return
            if (land.hasAccess(target.uniqueId) || land.hasAccess(attacker.uniqueId)) return
            !value
        }
    }

    @EventHandler
    fun handleEntityExplode(event: TNTPrimeEvent) {
        val block = event.block
        val land = pandorasClusterApi.getLand(block.chunk)
        val primerEntity = event.primerEntity
        if (land != null) {
            val landFlag = land.getLandFlag(LandFlag.EXPLOSIONS)
            event.isCancelled = if (landFlag.getValue<Boolean>() == false) {
                true
            } else if (primerEntity != null) {
                if (land.hasAccess(primerEntity.uniqueId)) return
                if (primerEntity.hasPermission(Permission.EXPLOSION)) return
                true
            } else false
        }
    }

    @EventHandler
    fun handleEntityExplode(event: EntityExplodeEvent) {
        event.blockList().groupBy { it.chunk }.filter {
            pandorasClusterApi.getLand(it.key)?.getLandFlag(LandFlag.EXPLOSIONS)?.getValue<Boolean>() == false
        }.forEach { event.blockList().removeAll(it.value) }
    }

    @EventHandler
    fun handleEntityTargetLivingEntity(event: EntityTargetLivingEntityEvent) {
        val target = event.target ?: return
        val land = pandorasClusterApi.getLand(target.chunk) ?: return
        val landFlag = pandorasClusterApi.getLandFlag(LandFlag.PVE, land) ?: return
        event.isCancelled = landFlag.getValue<Boolean>() == true
    }

    @EventHandler
    fun handleEntityInteract(event: EntityInteractEvent) {

        val block = event.block
        val land = pandorasClusterApi.getLand(block.chunk) ?: return
        val blockData = block.blockData

        if (blockData is TurtleEgg) {
            val turtleEggDestroyFlag = pandorasClusterApi.getLandFlag(LandFlag.TURTLE_EGG_DESTROY, land)
            if (turtleEggDestroyFlag != null && turtleEggDestroyFlag.getValue<Boolean>() == false) {
                event.isCancelled = true
                event.entity.velocity =
                    event.entity.velocity.subtract(event.entity.location.direction).multiply(0.5).normalize()
                return
            }
        }

        val farmLandDestroyFlag = pandorasClusterApi.getLandFlag(LandFlag.FARMLAND_DESTROY, land) ?: return
        event.isCancelled = blockData is Farmland && farmLandDestroyFlag.getValue<Boolean>() == false
    }

    @EventHandler
    fun handleEntityMount(event: EntityMountEvent) {

        val mount = event.mount
        val entity = event.entity

        if (mount !is Vehicle) return
        if (mount is Tameable) {
            val tamer = mount.owner
            if (mount.isTamed && tamer != null && tamer.uniqueId == entity.uniqueId) return
        }

        val land = this.pandorasClusterApi.getLand(mount.getChunk()) ?: return
        if (land.hasAccess(entity.uniqueId)) return
        if (Permission.ENTITY_MOUNT.hasPermission(entity)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleEntityChangeBlock(event: EntityChangeBlockEvent) {
        val land = pandorasClusterApi.getLand(event.block.chunk) ?: return
        val landFlag = pandorasClusterApi.getLandFlag(LandFlag.ENTITY_CHANGE_BLOCK, land)
        event.isCancelled = landFlag?.getValue<Boolean>() == false
    }

    @EventHandler
    fun handleVehicleDestroy(event: VehicleDestroyEvent) {
        val vehicle = event.vehicle
        val attacker = event.attacker
        val land = pandorasClusterApi.getLand(vehicle.chunk)
        if (land != null) {
            val landFlag = pandorasClusterApi.getLandFlag(LandFlag.VEHICLE_DAMAGE, land) ?: return
            if (landFlag.getValue<Boolean>() == true) return
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
            val landFlag = pandorasClusterApi.getLandFlag(LandFlag.VEHICLE_DAMAGE, land) ?: return
            if (landFlag.getValue<Boolean>() == true) return
            event.isCancelled = true
        }
    }

    @EventHandler
    fun handleVehicleCreation(event: VehicleCreateEvent) {
        val vehicle = event.vehicle
        val land = pandorasClusterApi.getLand(vehicle.chunk) ?: return
        val landFlag = pandorasClusterApi.getLandFlag(LandFlag.VEHICLE_CREATE, land) ?: return
        if (landFlag.getValue<Boolean>() == true) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleVehicleMovement(event: VehicleMoveEvent) {
        val vehicle = event.vehicle
        val to = event.to
        val toLand = pandorasClusterApi.getLand(to.chunk)
        if (toLand != null) {
            vehicle.passengers.filter { toLand.isBanned(it.uniqueId) }.forEach { vehicle.removePassenger(it) }
        } else if (vehicle is Minecart || vehicle is Boat) {
            vehicle.remove()
        }

    }

    @Suppress("kotlin:S3776")
    @EventHandler(priority = EventPriority.HIGHEST)
    fun handlePotionSplash(event: ProjectileLaunchEvent) {

        val projectile = event.entity

        if (projectile is ThrownPotion) {

            val source = projectile.getShooter()
            if (source is Entity) {
                val land = pandorasClusterApi.getLand(source.chunk)

                if (land != null) {

                    val landFlag = pandorasClusterApi.getLandFlag(LandFlag.POTION_SPLASH, land) ?: return
                    if (landFlag.getValue<Boolean>() == true) return

                    if (land.hasAccess(projectile.uniqueId)) return
                    if (Permission.POTION_SPLASH.hasPermission(projectile)) return
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun handleVehicleEnter(event: VehicleEnterEvent) {
        val vehicle = event.vehicle
        val entered = event.entered
        val land = pandorasClusterApi.getLand(vehicle.chunk)
        if (land != null) {
            val landFlag = pandorasClusterApi.getLandFlag(LandFlag.VEHICLE_USE, land)
            if (landFlag != null && landFlag.getValue<Boolean>() == true) return
            if (land.hasAccess(entered.uniqueId)) return
            if (Permission.VEHICLE_ENTER.hasPermission(entered)) return
            event.isCancelled = true
        }
    }

    @EventHandler
    fun handleEntityTame(event: EntityTameEvent) {
        val land = pandorasClusterApi.getLand(event.entity.chunk)
        val owner = event.owner
        if (owner !is Permissible) return
        if (Permission.TAME_ENTITY.hasPermission(owner)) return
        if (land == null || !land.isBanned(event.owner.uniqueId)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleEntityEnterBlock(event: EntityEnterBlockEvent) {
        val land = pandorasClusterApi.getLand(event.block.chunk) ?: return
        val landFlag = pandorasClusterApi.getLandFlag(LandFlag.BEE_INTERACT, land)
        event.isCancelled = landFlag?.getValue<Boolean>() == false
    }
}
