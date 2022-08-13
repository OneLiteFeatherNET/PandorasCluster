package net.onelitefeather.pandorascluster.listener

import com.destroystokyo.paper.event.block.TNTPrimeEvent
import com.destroystokyo.paper.event.entity.EntityPathfindEvent
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.service.LandService
import net.onelitefeather.pandorascluster.util.hasSameOwner
import org.bukkit.block.data.type.Farmland
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.EntityBlockFormEvent
import org.bukkit.event.entity.*
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.vehicle.*
import org.bukkit.permissions.Permissible
import org.spigotmc.event.entity.EntityMountEvent

class LandEntityListener(private val landService: LandService) :
    Listener {

    @EventHandler
    fun handleProjectileHit(event: ProjectileHitEvent) {
        val entity = event.entity
        val shooter = if (entity.shooter is Entity) {
            entity.shooter
        } else null

        val land: Land?
        val hitEntity = event.hitEntity
        if (hitEntity != null) {

            land = landService.getFullLand(hitEntity.chunk) ?: return
            if (shooter is Entity && land.hasAccess(shooter.uniqueId)) return

            val cancel = if (hitEntity !is Player) {
                val flag = landService.getLandFlag(LandFlag.PVE, land) ?: return
                val value = flag.getValue<Boolean>()!!
                shooter is Permissible && shooter.hasPermission(Permission.PVE) || !value || shooter is Entity
            } else {
                val flag = landService.getLandFlag(LandFlag.PVP, land) ?: return
                val value = flag.getValue<Boolean>()!!
                shooter is Permissible && shooter.hasPermission(Permission.PVP) || !value || shooter is Entity
            }

            event.isCancelled = cancel
            return
        }

        val hitBlock = event.hitBlock
        if (hitBlock != null) {
            land = landService.getFullLand(hitBlock.chunk) ?: return
            event.isCancelled = shooter is Entity && !land.hasAccess(shooter.uniqueId)
        }
    }

    @EventHandler
    fun theConfuser(event: EntityDamageByEntityEvent) {
        val target = event.entity
        val attacker = event.damager
        val land = landService.getFullLand(target.chunk) ?: landService.getFullLand(attacker.chunk) ?: return
        if (land.hasAccess(attacker.uniqueId) && land.hasAccess(target.uniqueId)) return
        if (attacker is Player) {
            event.isCancelled = if (target !is Player) {
                val flag = landService.getLandFlag(LandFlag.PVE, land) ?: return
                val value = flag.getValue<Boolean>()
                attacker.hasPermission(Permission.PVE) || value == false
            } else {
                val flag = landService.getLandFlag(LandFlag.PVP, land) ?: return
                val value = flag.getValue<Boolean>()
                attacker.hasPermission(Permission.PVP) || value == false
            }
        }
    }

    @EventHandler
    fun handleEntityExplode(event: TNTPrimeEvent) {
        val block = event.block
        val land = landService.getFullLand(block.chunk)
        val primerEntity = event.primerEntity
        if (land != null) {
            val landFlag = landService.getLandFlag(LandFlag.EXPLOSIONS, land)
            event.isCancelled = if (landFlag != null && landFlag.getValue<Boolean>() == false) {
                true
            } else if (primerEntity != null) {
                if (land.hasAccess(primerEntity.uniqueId)) return
                if (Permission.EXPLOSION.hasPermission(primerEntity)) return
                true
            } else false
        }
    }

    @EventHandler
    fun handleEntityExplode(event: EntityExplodeEvent) {
        val entity = event.entity
        val land = landService.getFullLand(entity.chunk)
        if (land != null) {
            val iterator = event.blockList().iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                val nextLand = landService.getFullLand(next.chunk)
                if (nextLand != null && !hasSameOwner(land, nextLand)) {
                    iterator.remove()
                }
            }
        }
    }

    @EventHandler
    fun handleEntityTargetLivingEntity(event: EntityTargetLivingEntityEvent) {
        val entity = event.entity
        val target = event.target ?: return
        val land = landService.getFullLand(entity.chunk) ?: landService.getFullLand(target.chunk) ?: return
        val landFlag = landService.getLandFlag(LandFlag.PVE, land)
        if (landFlag != null && landFlag.getValue<Boolean>() == true) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleEntityInteract(event: EntityInteractEvent) {
        val block = event.block
        val land = landService.getFullLand(block.chunk) ?: return
        val blockData = block.blockData
        val farmLandDestroyFlag = landService.getLandFlag(LandFlag.FARMLAND_DESTROY, land) ?: return
        event.isCancelled = blockData is Farmland && farmLandDestroyFlag.getValue<Boolean>() == false
    }

    @EventHandler
    fun handleHangingBreak(event: HangingBreakEvent) {
        if (event.cause == HangingBreakEvent.RemoveCause.ENTITY) return
        val entity = event.entity
        val land = landService.getFullLand(entity.chunk) ?: return
        val landFlag = landService.getLandFlag(LandFlag.HANGING_BREAK, land) ?: return
        if (landFlag.getValue<Boolean>() == true) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleEntityBlockForm(event: EntityBlockFormEvent) {
        val land = landService.getFullLand(event.block.chunk) ?: return
        val landFlag = landService.getLandFlag(LandFlag.BLOCK_FORM, land) ?: return
        if (land.hasAccess(event.entity.uniqueId)) return
        if (landFlag.getValue<Boolean>() == true) return
        event.isCancelled = true
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

        val land = this.landService.getFullLand(mount.getChunk()) ?: return
        if (land.hasAccess(entity.uniqueId)) return
        if (Permission.ENTITY_MOUNT.hasPermission(entity)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleEntityChangeBlock(event: EntityChangeBlockEvent) {
        val land = landService.getFullLand(event.block.chunk) ?: return
        val landFlag = landService.getLandFlag(LandFlag.BLOCK_FORM, land)
        if (landFlag != null && landFlag.getValue<Boolean>() == true) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleVehicleDestroy(event: VehicleDestroyEvent) {
        val vehicle = event.vehicle
        val attacker = event.attacker
        val land = landService.getFullLand(vehicle.chunk)
        if (land != null) {
            val landFlag = landService.getLandFlag(LandFlag.VEHICLE_DAMAGE, land) ?: return
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
        val land = landService.getFullLand(vehicle.chunk)
        if (land != null) {
            if (attacker != null) {
                if (land.hasAccess(attacker.uniqueId)) return
                if (Permission.VEHICLE_DAMAGE.hasPermission(attacker)) return
            }
            val landFlag = landService.getLandFlag(LandFlag.VEHICLE_DAMAGE, land) ?: return
            if (landFlag.getValue<Boolean>() == true) return
            event.isCancelled = true
        }
    }

    @EventHandler
    fun handleVehicleCreation(event: VehicleCreateEvent) {
        val vehicle = event.vehicle
        val land = landService.getFullLand(vehicle.chunk) ?: return
        val landFlag = landService.getLandFlag(LandFlag.VEHICLE_CREATE, land) ?: return
        if (landFlag.getValue<Boolean>() == true) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleVehicleMovement(event: VehicleMoveEvent) {
        val vehicle = event.vehicle
        val to = event.to
        val toLand = landService.getFullLand(to.chunk)
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
                val land = landService.getFullLand(source.chunk)

                if (land != null) {

                    val landFlag = landService.getLandFlag(LandFlag.POTION_SPLASH, land) ?: return
                    if (landFlag.getValue<Boolean>() == true) return

                    if (land.hasAccess(projectile.uniqueId)) return
                    if (Permission.POTION_SPLASH.hasPermission(projectile)) return
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun handleEntityPathfinding(event: EntityPathfindEvent) {
        val entity = event.entity
        val location = event.loc
        val land = landService.getFullLand(location.chunk)
        val entityLand = landService.getFullLand(entity.chunk)
        event.isCancelled = if (entityLand != null) {
            if (land != null) {
                !hasSameOwner(entityLand, land)
            } else {
                true
            }
        } else {
            land != null
        }
    }

    @EventHandler
    fun handleVehicleEnter(event: VehicleEnterEvent) {
        val vehicle = event.vehicle
        val entered = event.entered
        val land = landService.getFullLand(vehicle.chunk)
        if (land != null) {
            val landFlag = landService.getLandFlag(LandFlag.VEHICLE_USE, land)
            if (landFlag != null && landFlag.getValue()!!) return
            if (land.hasAccess(entered.uniqueId)) return
            if (Permission.VEHICLE_ENTER.hasPermission(entered)) return
            event.isCancelled = true
        }
    }
}