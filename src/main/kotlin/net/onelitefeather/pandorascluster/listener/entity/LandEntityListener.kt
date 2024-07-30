package net.onelitefeather.pandorascluster.listener.entity

import com.destroystokyo.paper.event.entity.EntityPathfindEvent
import net.onelitefeather.pandorascluster.api.EntityCategory
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlag
import net.onelitefeather.pandorascluster.util.getEntityCount
import net.onelitefeather.pandorascluster.util.getEntityLimit
import net.onelitefeather.pandorascluster.util.hasSameOwner
import org.bukkit.Chunk
import org.bukkit.block.Block
import org.bukkit.block.data.type.CaveVinesPlant
import org.bukkit.block.data.type.Farmland
import org.bukkit.block.data.type.TurtleEgg
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.EntityBlockFormEvent
import org.bukkit.event.entity.*
import org.bukkit.permissions.Permissible

class LandEntityListener(private val pandorasClusterApi: PandorasClusterApi) : Listener, EntityUtils {

    @EventHandler
    fun handleProjectileHit(event: ProjectileHitEvent) {
        val entity = event.entity
        val shooter = if (entity.shooter is Entity) {
            entity.shooter
        } else null

        val land: Land?
        val hitEntity = event.hitEntity
        if (hitEntity != null) {

            land = pandorasClusterApi.getLand(hitEntity.chunk) ?: return
            if (shooter is Entity && land.hasAccess(shooter.uniqueId)) return

            val cancel = if (hitEntity !is Player) {
                val flag = land.getLandFlag(LandFlag.PVE)
                val value = flag.getValue<Boolean>()!!
                shooter is Permissible && hasPermission(shooter, LandFlag.PVE) || !value || shooter is Entity
            } else {
                val flag = land.getLandFlag(LandFlag.PVP)
                val value = flag.getValue<Boolean>()!!
                shooter is Permissible && hasPermission(shooter, LandFlag.PVP) || !value || shooter is Entity
            }

            event.isCancelled = cancel
            return
        }

        val hitBlock = event.hitBlock
        if (hitBlock != null) {
            land = pandorasClusterApi.getLand(hitBlock.chunk) ?: return
            event.isCancelled = shooter is Entity && !land.hasAccess(shooter.uniqueId)
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
            !hasPermission(attacker, pvpFlag)
        } else {
            if (land.getLandFlag(pvpFlag).getValue<Boolean>() == true) return
            if (land.hasAccess(target.uniqueId) || land.hasAccess(attacker.uniqueId)) return
            !hasPermission(target, pveFlag) || !hasPermission(attacker, pveFlag)
        }
    }

    @EventHandler
    fun handleEntityExplode(event: EntityExplodeEvent) {
        if(event.entity is TNTPrimed) {
            event.blockList().groupBy(Block::getChunk).filter(this::filterForNoExplosiveLands).forEach { event.blockList().removeAll(it.value) }
        }
    }

    private fun filterForNoExplosiveLands(land: Map.Entry<Chunk, List<Block>>): Boolean {
        return pandorasClusterApi.getLand(land.key) == null ||
                pandorasClusterApi.getLand(land.key)?.getLandFlag(LandFlag.EXPLOSIONS)?.getValue<Boolean>() == false
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
            !hasPermission(entity, landFlag)
        }
    }

    @EventHandler
    fun handleEntityChangeBlock(event: EntityChangeBlockEvent) {

        val block = event.block
        val entity = event.entity
        val blockData = block.blockData
        val land = pandorasClusterApi.getLand(event.block.chunk)

        event.isCancelled = if (blockData is CaveVinesPlant) {
            cancelCropInteract(entity, land)
        } else {
            val landFlagECB = LandFlag.ENTITY_CHANGE_BLOCK
            if (land != null) {
                if (land.hasAccess(entity.uniqueId)) return
                if (land.getLandFlag(landFlagECB).getValue<Boolean>() == true) return
                !hasPermission(entity, landFlagECB)
            } else {
                !hasPermission(entity, landFlagECB) && entity is Player
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
                    event.isCancelled = !hasPermission(source, landFlag)
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
        event.isCancelled = !hasPermission(owner, LandFlag.ENTITY_TAME)
    }

    @EventHandler
    fun handleEntityPathfinding(event: EntityPathfindEvent) {
        val entity = event.entity
        val location = event.loc
        val land = pandorasClusterApi.getLand(location.chunk)
        val entityLand = pandorasClusterApi.getLand(entity.chunk)
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
    fun handleEntityBlockForm(event: EntityBlockFormEvent) {

        val block = event.block
        val land = pandorasClusterApi.getLand(block.chunk) ?: return

        if (block.type.name.endsWith("ICE")) {
            val iceFormFlag = LandFlag.ICE_FORM
            event.isCancelled = if (land.getLandFlag(iceFormFlag).getValue<Boolean>() == false) {
                false
            } else if (!land.hasAccess(event.entity.uniqueId)) {
                false
            } else !hasPermission(event.entity, iceFormFlag)
        } else {
            val blockFormFlag = land.getLandFlag(LandFlag.BLOCK_FORM)
            if (land.hasAccess(event.entity.uniqueId)) return
            if (blockFormFlag.getValue<Boolean>() == true) return
            event.isCancelled = true
        }
    }

    @EventHandler
    fun handleEntitySpawn(event: EntitySpawnEvent) {

        val entity = event.entity

        val land = pandorasClusterApi.getLand(entity.chunk) ?: return
        var entityCount = 0
        var limit = 0
        var category: EntityCategory? = null

        if (entity is Animals) {
            category = EntityCategory.ANIMALS
            entityCount = getEntityCount(land, category)
            limit = getEntityLimit(land, category)
        }

        if (entity is Monster) {
            category = EntityCategory.MONSTER
            entityCount = getEntityCount(land, category)
            limit = getEntityLimit(land, category)
        }

        if (entity is AbstractVillager) {
            category = EntityCategory.VILLAGER
            entityCount = getEntityCount(land, category)
            limit = getEntityLimit(land, category)
        }

        if (entityCount >= limit) {
            // Add 1 to the current entity count and check if the entity can spawn
            event.isCancelled = (entityCount + 1) > limit
            if (category != null) {
                pandorasClusterApi.getStaffNotificaton().notify(land, category)
            }
        }
    }

    private fun cancelCropInteract(entity: Entity, land: Land?): Boolean {
        val landFlag = LandFlag.INTERACT_CROPS
        if (land != null) {
            if (land.hasAccess(entity.uniqueId)) return false
            if (land.getLandFlag(landFlag).getValue<Boolean>() == true) return false
            !hasPermission(entity, landFlag)
        } else {
            !hasPermission(entity, landFlag) && entity is Player
        }

        return true
    }
}