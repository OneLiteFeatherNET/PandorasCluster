package net.onelitefeather.pandorascluster.listener.entity

import com.destroystokyo.paper.event.block.TNTPrimeEvent
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.hasPermission
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.util.isPetOwner
import org.bukkit.block.data.type.CaveVinesPlant
import org.bukkit.block.data.type.Farmland
import org.bukkit.block.data.type.TurtleEgg
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.permissions.Permissible
import org.spigotmc.event.entity.EntityMountEvent

class LandEntityListener(private val pandorasClusterApi: PandorasClusterApi) : Listener {

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
    fun handleEntityDamageByEntity(event: EntityDamageByEntityEvent) {

        val target = event.entity
        var attacker = event.damager

        val pvpFlag = LandFlag.PVP
        val pveFlag = LandFlag.PVE

        if (attacker is Projectile) {
            attacker = attacker.shooter as Entity
        }

        val land = pandorasClusterApi.getLand(target.chunk) ?: pandorasClusterApi.getLand(attacker.chunk) ?: return
        event.isCancelled = if (target is Player && attacker is Player) {
            if (land.getLandFlag(pvpFlag).getValue<Boolean>() == true) return
            if (land.hasAccess(attacker.uniqueId) && land.hasAccess(target.uniqueId)) return
            !attacker.hasPermission(pvpFlag)
        } else {
            if (land.getLandFlag(pvpFlag).getValue<Boolean>() == true) return
            if (land.hasAccess(target.uniqueId) || land.hasAccess(attacker.uniqueId)) return
            !target.hasPermission(pveFlag) || !attacker.hasPermission(pveFlag)
        }
    }

    @EventHandler
    fun handleEntityExplode(event: TNTPrimeEvent) {

        val block = event.block
        val land = pandorasClusterApi.getLand(block.chunk)
        val primerEntity = event.primerEntity
        val landFlag = LandFlag.EXPLOSIONS

        if (land != null) {
            event.isCancelled = if (land.getLandFlag(landFlag).getValue<Boolean>() == false) {
                true
            } else if (primerEntity != null) {
                if (land.hasAccess(primerEntity.uniqueId)) return
                !primerEntity.hasPermission(landFlag)
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
        val landFlag = land.getLandFlag(LandFlag.PVE)
        event.isCancelled = landFlag.getValue<Boolean>() == true
    }

    @EventHandler
    fun handleEntityInteract(event: EntityInteractEvent) {

        val block = event.block
        val land = pandorasClusterApi.getLand(block.chunk) ?: return
        val blockData = block.blockData

        if (blockData is TurtleEgg && land.getLandFlag(LandFlag.TURTLE_EGG_DESTROY).getValue<Boolean>() == false) {
            event.isCancelled = true
            event.entity.velocity =
                event.entity.velocity.subtract(event.entity.location.direction).normalize().multiply(pandorasClusterApi.getPlugin().config.getDouble("zombie-velocity-multiplier"))
            return
        }

        val farmLandDestroyFlag = land.getLandFlag(LandFlag.INTERACT_CROPS)
        event.isCancelled = blockData is Farmland && farmLandDestroyFlag.getValue<Boolean>() == false
    }

    @EventHandler
    fun handleEntityMount(event: EntityMountEvent) {

        val mount = event.mount
        val entity = event.entity

        val landFlag = LandFlag.ENTITY_MOUNT
        val land = this.pandorasClusterApi.getLand(mount.chunk) ?: return
        if (land.hasAccess(entity.uniqueId)) return
        if (land.getLandFlag(landFlag).getValue<Boolean>() == true) return

        event.isCancelled = if (mount is Tameable && entity is AnimalTamer) {
            !mount.isTamed || !isPetOwner(mount, entity)
        } else {
            !entity.hasPermission(landFlag)
        }
    }

    @EventHandler
    fun handleEntityChangeBlock(event: EntityChangeBlockEvent) {

        val block = event.block
        val entity = event.entity
        val blockData = block.blockData
        val land = pandorasClusterApi.getLand(event.block.chunk)

        event.isCancelled = if (blockData is CaveVinesPlant) {
            val landFlag = LandFlag.INTERACT_CROPS
            if (land != null) {
                if (land.hasAccess(entity.uniqueId)) return
                if (land.getLandFlag(landFlag).getValue<Boolean>() == true) return
                !entity.hasPermission(landFlag)
            } else {
                !entity.hasPermission(landFlag) && entity is Player
            }
        } else {
            val landFlagECB = LandFlag.ENTITY_CHANGE_BLOCK
            if (land != null) {
                if (land.hasAccess(entity.uniqueId)) return
                if (land.getLandFlag(landFlagECB).getValue<Boolean>() == true) return
                !entity.hasPermission(landFlagECB)
            } else {
                !entity.hasPermission(landFlagECB) && entity is Player
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handlePotionSplash(event: ProjectileLaunchEvent) {
        val projectile = event.entity
        if (projectile is ThrownPotion) {

            val landFlag = LandFlag.POTION_SPLASH
            val source = projectile.getShooter()
            if (source is Entity) {
                val land = pandorasClusterApi.getLand(source.chunk)
                if (land != null) {
                    if (land.getLandFlag(landFlag).getValue<Boolean>() == true) return
                    if (land.hasAccess(source.uniqueId)) return
                    if (source.hasPermission(landFlag)) return
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun handleEntityTame(event: EntityTameEvent) {
        val land = pandorasClusterApi.getLand(event.entity.chunk) ?: return
        if (land.getLandFlag(LandFlag.ENTITY_TAME).getValue<Boolean>() == true) return
        val owner = event.owner
        if (owner !is Permissible) return
        if (land.hasAccess(owner.uniqueId)) return
        event.isCancelled = !owner.hasPermission(LandFlag.ENTITY_TAME)
    }
}
