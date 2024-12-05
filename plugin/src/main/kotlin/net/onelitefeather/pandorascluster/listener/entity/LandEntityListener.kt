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
        val land = pandorasClusterApi.getLandService().getLand(target.chunk.chunkKey) ?: return

        event.isCancelled = if(target is Player) {
            !land.hasMemberAccess(target.uniqueId, LandFlag.PVE)
        } else {
            !land.hasFlag(LandFlag.PVE)
        }
    }

    @EventHandler
    fun handleEntityInteract(event: EntityInteractEvent) {

        val block = event.block
        val land = pandorasClusterApi.getLandService().getLand(block.chunk.chunkKey) ?: return
        val blockData = block.blockData

        if (blockData is TurtleEgg && !land.hasFlag(LandFlag.TURTLE_EGG_DESTROY)) {
            event.isCancelled = true

            val targetVelocity = event.entity.location.direction
            val velocityMultiplier = plugin.config.getDouble("zombie-velocity-multiplier")

            event.entity.velocity =
                event.entity.velocity.subtract(targetVelocity).normalize().multiply(velocityMultiplier)
            return
        }

        event.isCancelled = blockData is Farmland && !land.hasFlag(LandFlag.INTERACT_CROPS)
    }

    @EventHandler
    fun handleEntityMount(event: EntityMountEvent) {

        val mount = event.mount
        val entity = event.entity

        val land = pandorasClusterApi.getLandService().getLand(mount.chunk.chunkKey) ?: return
        if (land.hasMemberAccess(entity.uniqueId, LandFlag.ENTITY_MOUNT)) return
        if (land.hasFlag(LandFlag.ENTITY_MOUNT)) return

        event.isCancelled = if (mount is Tameable && entity is AnimalTamer) {
            !mount.isTamed || !isPetOwner(mount, entity)
        } else {
            !hasPermission(entity, LandFlag.ENTITY_MOUNT)
        }
    }

    @EventHandler
    fun handleEntityChangeBlock(event: EntityChangeBlockEvent) {

        val block = event.block
        val entity = event.entity
        val blockData = block.blockData
        val land = pandorasClusterApi.getLandService().getLand(event.block.chunk.chunkKey)

        event.isCancelled = if (blockData is CaveVinesPlant) {
            cancelCropInteract(entity, land)
        } else {
            if (land != null) {
                if (land.hasFlag(LandFlag.ENTITY_CHANGE_BLOCK)) return
                !land.hasMemberAccess(entity.uniqueId, LandFlag.ENTITY_CHANGE_BLOCK)
            } else {
                !hasPermission(entity, LandFlag.ENTITY_CHANGE_BLOCK) && entity is Player
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
                val land = pandorasClusterApi.getLandService().getLand(source.chunk.chunkKey)
                if (land != null) {
                    event.isCancelled = !land.hasMemberAccess(source.uniqueId, LandFlag.POTION_SPLASH)
                }
            }
        }
    }

    @EventHandler
    fun handleEntityTame(event: EntityTameEvent) {
        val land = pandorasClusterApi.getLandService().getLand(event.entity.chunk.chunkKey) ?: return
        val owner = event.owner
        if (owner !is Permissible) return
        event.isCancelled = !land.hasMemberAccess(owner.uniqueId, LandFlag.ENTITY_TAME)
    }

    @EventHandler
    fun handleEntityPathfinding(event: EntityPathfindEvent) {
        val entity = event.entity
        val location = event.loc
        val land = pandorasClusterApi.getLandService().getLand(location.chunk.chunkKey)
        val entityLand = pandorasClusterApi.getLandService().getLand(entity.chunk.chunkKey)
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
        val land = pandorasClusterApi.getLandService().getLand(block.chunk.chunkKey) ?: return

        if (block.type.name.endsWith("ICE")) {
            event.isCancelled = if (!land.hasFlag(LandFlag.ICE_FORM)) {
                false
            } else if (!land.hasMemberAccess(event.entity.uniqueId)) {
                false
            } else !hasPermission(event.entity, LandFlag.ICE_FORM)
        } else {
            event.isCancelled = !land.hasMemberAccess(event.entity.uniqueId, LandFlag.BLOCK_FORM)
        }
    }

    //FIXME
//    @EventHandler
    fun handleEntitySpawn(event: EntitySpawnEvent) {

        val entity = event.entity

        val land = pandorasClusterApi.getLandService().getLand(entity.chunk.chunkKey) ?: return
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
        if (land != null) {
            !land.hasMemberAccess(entity.uniqueId, LandFlag.INTERACT_CROPS)
        } else {
            !hasPermission(entity, LandFlag.INTERACT_CROPS) && entity is Player
        }

        return true
    }

    private fun filterForNoExplosiveLands(landEntry: Map.Entry<Chunk, List<Block>>): Boolean {
        val land = pandorasClusterApi.getLandService().getLand(landEntry.key.chunkKey)
        return land == null || !land.hasFlag(LandFlag.EXPLOSIONS)
    }
}