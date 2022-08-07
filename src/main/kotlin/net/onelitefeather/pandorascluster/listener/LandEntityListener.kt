package net.onelitefeather.pandorascluster.listener

import com.destroystokyo.paper.event.block.TNTPrimeEvent
import com.destroystokyo.paper.event.entity.EntityPathfindEvent
import net.onelitefeather.pandorascluster.enums.Permission
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.service.LandFlagService
import net.onelitefeather.pandorascluster.service.LandService
import net.onelitefeather.pandorascluster.util.ChunkUtil
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.block.data.Powerable
import org.bukkit.block.data.type.Farmland
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.EntityBlockFormEvent
import org.bukkit.event.entity.*
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.vehicle.*
import org.bukkit.inventory.ItemStack
import org.bukkit.permissions.Permissible
import org.bukkit.projectiles.ProjectileSource
import org.jetbrains.annotations.NotNull
import org.spigotmc.event.entity.EntityMountEvent

class LandEntityListener(private val landService: LandService, private val landFlagService: LandFlagService) :
    Listener {

    @EventHandler
    fun handleProjectileHit(event: ProjectileHitEvent) {

        val entity = event.entity
        var shooter: ProjectileSource? = null

        if (entity.shooter is Entity) {
            shooter = entity.shooter
        }

        val hitEntity = event.hitEntity
        val hitBlock = event.hitBlock

        var land: Land? = null

        if (hitEntity != null) {
            land = landService.getFullLand(hitEntity.chunk)
        }

        if (hitBlock != null) {
            land = landService.getFullLand(hitBlock.chunk)
        }

        if (land != null) {

            var cancel = false
            if (shooter is Permissible) {
                cancel = !Permission.PROJECTILE_HIT_ENTITY.hasPermission(shooter)
            }

            if (hitBlock != null) {
                val blockData = hitBlock.blockData
                if (blockData is Powerable) {
                    val landFlag = landFlagService.getFlag(LandFlag.REDSTONE, land) ?: return
                    cancel = !landFlag.getValue<Boolean>()
                }
            }

            event.isCancelled = cancel
        }
    }

    @EventHandler
    fun theConfuser(event: EntityDamageByEntityEvent) {
        val target = event.entity
        val attacker = event.damager
        var land = landService.getFullLand(target.chunk)
        if (land == null) land = landService.getFullLand(attacker.chunk)
        if (land == null) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleEntityExplode(event: TNTPrimeEvent) {
        val block = event.block
        val land = landService.getFullLand(block.chunk)
        val primerEntity = event.primerEntity
        if (land != null) {
            val landFlag = landFlagService.getFlag(LandFlag.EXPLOSIONS, land)
            var cancel = landFlag != null && !landFlag.getValue<Boolean>()
            if (primerEntity != null) {
                if (land.hasAccess(primerEntity.uniqueId)) return
                if (Permission.EXPLOSION.hasPermission(primerEntity)) return
                cancel = true
            }
            event.isCancelled = cancel
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
                if (nextLand != null && !ChunkUtil.hasSameOwner(land, nextLand)) {
                    iterator.remove()
                }
            }
        }
    }

    @EventHandler
    fun handleEntityTargetLivingEntity(event: EntityTargetLivingEntityEvent) {
        val entity = event.entity
        val target = event.target
        var land = landService.getFullLand(entity.chunk)
        if (target != null && land == null) {
            land = landService.getFullLand(target.chunk)
        }
        if (land == null) return
        val landFlag = landFlagService.getFlag(LandFlag.PVE, land)
        if (landFlag != null && landFlag.getValue()) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleEntityInteract(event: EntityInteractEvent) {
        val entity = event.entity
        val block = event.block
        val land = landService.getFullLand(block.chunk) ?: return
        val blockData = block.blockData
        val farmLandDestroyFlag = landFlagService.getFlag(LandFlag.FARMLAND_DESTROY, land) ?: return
        val redstoneFlag = landFlagService.getFlag(LandFlag.REDSTONE, land) ?: return
        if (blockData is Farmland && !farmLandDestroyFlag.getValue<Boolean>() ||
            blockData is Powerable && !redstoneFlag.getValue<Boolean>()
        ) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun handleHangingBreak(event: HangingBreakEvent) {
        val entity = event.entity
        val land = landService.getFullLand(entity.chunk) ?: return
        val landFlag = landFlagService.getFlag(LandFlag.HANGING_BREAK, land)
        if (landFlag != null && landFlag.getValue()) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleEntityBlockForm(event: EntityBlockFormEvent) {
        val land = landService.getFullLand(event.block.chunk) ?: return
        val landFlag = landFlagService.getFlag(LandFlag.BLOCK_FORM, land)
        if (landFlag != null) {
            if (land.hasAccess(event.entity.uniqueId)) return
            val flag = landFlag.getValue<Boolean>()
            if (flag) return
            event.isCancelled = true
        }
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

        var land = this.landService.getFullLand(mount.getChunk()) ?: return
        if (land.hasAccess(entity.uniqueId)) return
        if (Permission.ENTITY_MOUNT.hasPermission(entity)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleEntityChangeBlock(event: EntityChangeBlockEvent) {
        val land = landService.getFullLand(event.block.chunk) ?: return
        val landFlag = landFlagService.getFlag(LandFlag.BLOCK_FORM, land)
        if (landFlag != null && landFlag.getValue()) return
        event.isCancelled = true
    }

    @EventHandler
    fun handleVehicleDestroy(event: VehicleDestroyEvent) {
        val vehicle = event.vehicle
        val attacker = event.attacker
        val land = landService.getFullLand(vehicle.chunk)
        if (land != null) {
            val landFlag = landFlagService.getFlag(LandFlag.VEHICLE_DAMAGE, land)
            if (landFlag != null && landFlag.getValue()) return
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
            val landFlag = landFlagService.getFlag(LandFlag.VEHICLE_DAMAGE, land)
            if (landFlag != null && landFlag.getValue()) return
            event.isCancelled = true
        }
    }

    @EventHandler
    fun handleVehicleCreation(event: VehicleCreateEvent) {
        val vehicle = event.vehicle
        val land = landService.getFullLand(vehicle.chunk)
        if (land != null) {
            val landFlag = landFlagService.getFlag(LandFlag.VEHICLE_CREATE, land)
            if (landFlag != null && landFlag.getValue()) return
            event.isCancelled = true
        }
    }

    @EventHandler
    fun handleVehicleMovement(event: VehicleMoveEvent) {
        val vehicle = event.vehicle
        val to = event.to
        val from = event.from
        val toLand = landService.getFullLand(to.chunk)
        val fromLand = landService.getFullLand(from.chunk)
        if (toLand != null) {
            for (entity in vehicle.passengers) {
                if (toLand.isBanned(entity.uniqueId)) {
                    vehicle.removePassenger(entity)
                }
            }
        }
        if (fromLand != null) {
            removeVehicle(vehicle, if (toLand != null && !ChunkUtil.hasSameOwner(toLand, fromLand)) to else from)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handlePotionSplash(event: ProjectileLaunchEvent) {

        val projectile = event.entity

        if (projectile is ThrownPotion) {

            val source = projectile.getShooter()
            if (source is Entity) {
                val land = landService.getFullLand(source.chunk)

                if (land != null) {

                    val landFlag = landFlagService.getFlag(LandFlag.POTION_SPLASH, land) ?: return
                    if (landFlag.getValue()) return

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
        var cancel = false
        if (entityLand != null) {
            if (land != null) {
                if (!ChunkUtil.hasSameOwner(entityLand, land)) {
                    cancel = true
                }
            } else {
                cancel = true
            }
        } else {
            if (land != null) {
                cancel = true
            }
        }
        event.isCancelled = cancel
    }

    @EventHandler
    fun handleVehicleEnter(event: VehicleEnterEvent) {
        val vehicle = event.vehicle
        val entered = event.entered
        val land = landService.getFullLand(vehicle.chunk)
        if (land != null) {
            val landFlag = landFlagService.getFlag(LandFlag.VEHICLE_USE, land)
            if (landFlag != null && landFlag.getValue()) return
            if (land.hasAccess(entered.uniqueId)) return
            if (Permission.VEHICLE_ENTER.hasPermission(entered)) return
            event.isCancelled = true
        }
    }

    private fun removeVehicle(vehicle: @NotNull Vehicle, location: @NotNull Location) {
        val passengers = vehicle.passengers

        var cancelDrop = false
        if (passengers.isNotEmpty()) {
            for (i in passengers.indices.reversed()) {
                val passenger = passengers[i]
                if (passenger is Player) {
                    if (passenger.gameMode == GameMode.CREATIVE) {
                        cancelDrop = true
                    }
                }
            }
        }

        if (!cancelDrop) {

            var dropItem: ItemStack? = null
            if (vehicle is Minecart) {
                dropItem = ItemStack(vehicle.minecartMaterial)
            } else if (vehicle is Boat) {
                dropItem = ItemStack(vehicle.boatType.material)
            }

            if (dropItem != null) {
                location.world.dropItemNaturally(location, dropItem)
            }
        }

        vehicle.remove()
    }
}