package net.onelitefeather.pandorascluster.listener.entity

import com.destroystokyo.paper.event.entity.EntityPathfindEvent
import net.onelitefeather.pandorascluster.PandorasClusterPlugin
import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.enums.EntityCategory
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import org.bukkit.Chunk
import org.bukkit.block.Block
import org.bukkit.block.data.type.CaveVinesPlant
import org.bukkit.block.data.type.Farmland
import org.bukkit.block.data.type.TurtleEgg
import org.bukkit.entity.AbstractVillager
import org.bukkit.entity.AnimalTamer
import org.bukkit.entity.Animals
import org.bukkit.entity.Entity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.entity.TNTPrimed
import org.bukkit.entity.Tameable
import org.bukkit.entity.ThrownPotion
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.EntityBlockFormEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.entity.EntityMountEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTameEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.permissions.Permissible

class LandEntityListener(private val pandorasClusterApi: PandorasClusterApi,
                         private val plugin: PandorasClusterPlugin) : Listener, EntityUtils, ChunkUtils {

    @EventHandler
    fun handleEntityExplode(event: EntityExplodeEvent) {
        if (event.entity is TNTPrimed) {
            event.blockList().groupBy(Block::getChunk).filter(this::filterForNoExplosiveLands)
                .forEach { event.blockList().removeAll(it.value) }
        }
    }

    @EventHandler
    fun handleEntityTargetLivingEntity(event: EntityTargetLivingEntityEvent) {
        val target = event.target ?: return
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(target.chunk)) ?: return
        val landFlag = land.getFlag(LandFlag.PVE)
        event.isCancelled = landFlag.getValue<Boolean>() == true
    }

    @EventHandler
    fun handleEntityInteract(event: EntityInteractEvent) {

        val block = event.block
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(block.chunk)) ?: return
        val blockData = block.blockData

        if (blockData is TurtleEgg && land.getFlag(LandFlag.TURTLE_EGG_DESTROY).getValue<Boolean>() == false) {
            event.isCancelled = true

            val targetVelocity = event.entity.location.direction
            val velocityMultiplier = plugin.config.getDouble("zombie-velocity-multiplier")

            event.entity.velocity =
                event.entity.velocity.subtract(targetVelocity).normalize().multiply(velocityMultiplier)
            return
        }

        val farmLandDestroyFlag = land.getFlag(LandFlag.INTERACT_CROPS)
        event.isCancelled = blockData is Farmland && farmLandDestroyFlag.getValue<Boolean>() == false
    }

    @EventHandler
    fun handleEntityMount(event: EntityMountEvent) {

        val mount = event.mount
        val entity = event.entity

        val landFlag = LandFlag.ENTITY_MOUNT
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(mount.chunk)) ?: return
        if (land.hasMemberAccess(entity.uniqueId)) return
        if (land.getFlag(landFlag).getValue<Boolean>() == true) return

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
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.block.chunk))

        event.isCancelled = if (blockData is CaveVinesPlant) {
            cancelCropInteract(entity, land)
        } else {
            val landFlagECB = LandFlag.ENTITY_CHANGE_BLOCK
            if (land != null) {
                if (land.hasMemberAccess(entity.uniqueId)) return
                if (land.getFlag(landFlagECB).getValue<Boolean>() == true) return
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
                val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(source.chunk))
                if (land != null) {
                    if (land.getFlag(landFlag).getValue<Boolean>() == true) return
                    if (land.hasMemberAccess(source.uniqueId)) return
                    event.isCancelled = !hasPermission(source, landFlag)
                }
            }
        }
    }

    @EventHandler
    fun handleEntityTame(event: EntityTameEvent) {
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(event.entity.chunk)) ?: return
        if (land.getFlag(LandFlag.ENTITY_TAME).getValue<Boolean>() == true) return
        val owner = event.owner
        if (owner !is Permissible) return
        if (land.hasMemberAccess(owner.uniqueId)) return
        event.isCancelled = !hasPermission(owner, LandFlag.ENTITY_TAME)
    }

    @EventHandler
    fun handleEntityPathfinding(event: EntityPathfindEvent) {
        val entity = event.entity
        val location = event.loc
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(location.chunk))
        val entityLand = pandorasClusterApi.getLandService().getLand(toClaimedChunk(entity.chunk))
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
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(block.chunk)) ?: return

        if (block.type.name.endsWith("ICE")) {
            val iceFormFlag = LandFlag.ICE_FORM
            event.isCancelled = if (land.getFlag(iceFormFlag).getValue<Boolean>() == false) {
                false
            } else if (!land.hasMemberAccess(event.entity.uniqueId)) {
                false
            } else !hasPermission(event.entity, iceFormFlag)
        } else {
            val blockFormFlag = land.getFlag(LandFlag.BLOCK_FORM)
            if (land.hasMemberAccess(event.entity.uniqueId)) return
            if (blockFormFlag.getValue<Boolean>() == true) return
            event.isCancelled = true
        }
    }

    @EventHandler
    fun handleEntitySpawn(event: EntitySpawnEvent) {

        val entity = event.entity

        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(entity.chunk)) ?: return
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
            if (category != null) {
                event.isCancelled = (entityCount + 1) > limit
                pandorasClusterApi.getStaffNotification().notify(land, category)
            }
        }
    }

    private fun cancelCropInteract(entity: Entity, land: Land?): Boolean {
        val landFlag = LandFlag.INTERACT_CROPS
        if (land != null) {
            if (land.hasMemberAccess(entity.uniqueId)) return false
            if (land.getFlag(landFlag).getValue<Boolean>() == true) return false
            !hasPermission(entity, landFlag)
        } else {
            !hasPermission(entity, landFlag) && entity is Player
        }

        return true
    }

    private fun filterForNoExplosiveLands(landEntry: Map.Entry<Chunk, List<Block>>): Boolean {
        val land = pandorasClusterApi.getLandService().getLand(toClaimedChunk(landEntry.key))
        return land == null || land.getFlag(LandFlag.EXPLOSIONS).getValue<Boolean>() == false
    }
}