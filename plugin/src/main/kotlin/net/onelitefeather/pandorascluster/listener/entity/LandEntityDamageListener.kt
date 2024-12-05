package net.onelitefeather.pandorascluster.listener.entity

import net.onelitefeather.pandorascluster.api.land.Land
import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.projectiles.BlockProjectileSource

@Suppress("UnstableApiUsage")
class LandEntityDamageListener(private val pandorasClusterApi: PandorasClusterApi) : Listener, EntityUtils, ChunkUtils {

    @EventHandler
    fun handleEntityDamage(event: EntityDamageEvent) {

        val causingEntity = event.damageSource.causingEntity ?: return

        val entity = event.entity
        val land = pandorasClusterApi.getLandService().getLand(causingEntity.chunk.chunkKey) ?: return

        if (causingEntity !is Arrow) return

        event.isCancelled = if (causingEntity.shooter is BlockProjectileSource) {
            if(land.hasMemberAccess(entity.uniqueId, LandFlag.PVE)) return
            !land.hasFlag(LandFlag.PVE)
        } else {
            event.isCancelled
        }
    }

    @EventHandler
    fun handleMeleeAttack(event: EntityDamageByEntityEvent) {

        val target = event.entity
        val attacker = event.damager

        if (attacker is Projectile) return

        val land = pandorasClusterApi.getLandService().getLand(target.chunk.chunkKey) ?: return
        event.isCancelled = !canDamage(land, attacker, target)
    }

    @EventHandler
    fun handleProjectileEntityHit(event: ProjectileHitEvent) {

        val entity = event.entity
        val shooter = if (entity.shooter is Entity) {
            entity.shooter as Entity
        } else null ?: return

        val hitEntity = event.hitEntity ?: return
        val land = pandorasClusterApi.getLandService().getLand(hitEntity.chunk.chunkKey) ?: return

        event.isCancelled = !canDamage(land, shooter, hitEntity)
    }

    @EventHandler
    fun handleProjectileBlockHit(event: ProjectileHitEvent) {

        val entity = event.entity
        val shooter = if (entity.shooter is Entity) {
            entity.shooter as Entity
        } else null ?: return

        val hitBlock = event.hitBlock ?: return
        val land = pandorasClusterApi.getLandService().getLand(hitBlock.chunk.chunkKey) ?: return

        //TODO: Make a own flag
        if(land.hasMemberAccess(shooter.uniqueId, LandFlag.BLOCK_BREAK)) return
        event.isCancelled = !land.hasFlag(LandFlag.BLOCK_BREAK)
    }

    private fun canDamage(land: Land, attacker: Entity, target: Entity): Boolean {

        if (target is Player && attacker is Player) {
            if(land.hasMemberAccess(attacker.uniqueId, LandFlag.PVP)) return true

            val pvpAllowed = land.hasFlag(LandFlag.PVP)
            return pvpAllowed
        }

        val pveAllowed = land.hasFlag(LandFlag.PVE)
        val targetPlayer = targetPlayer(attacker, target)
        if (targetPlayer != null) {
            if (land.hasMemberAccess(targetPlayer.uniqueId, LandFlag.PVE)) return true
            return pveAllowed
        }

        return pveAllowed
    }

    private fun targetPlayer(attacker: Entity, target: Entity): Player? {
        val targetPlayer = if (attacker is Player || target is Player) {
            if (attacker is Player) {
                attacker
            } else {
                target
            }
        } else {
            null
        }
        return targetPlayer as Player?
    }
}