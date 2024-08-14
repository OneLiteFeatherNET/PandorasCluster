package net.onelitefeather.pandorascluster.listener.entity

import net.onelitefeather.pandorascluster.api.PandorasClusterApi
import net.onelitefeather.pandorascluster.extensions.ChunkUtils
import net.onelitefeather.pandorascluster.extensions.EntityUtils
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlag
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
        val land = pandorasClusterApi.getLand(causingEntity.chunk) ?: return

        if (causingEntity !is Arrow) return

        event.isCancelled = if (causingEntity.shooter is BlockProjectileSource) {
            if (hasPermission(entity, LandFlag.PVE)) return
            if (land.hasAccess(entity.uniqueId)) return
            land.getLandFlag(LandFlag.PVE).getValue<Boolean>() == false
        } else {
            event.isCancelled
        }
    }

    @EventHandler
    fun handleCloseCombat(event: EntityDamageByEntityEvent) {

        val target = event.entity
        val attacker = event.damager

        if (attacker is Projectile) return

        val land = pandorasClusterApi.getLand(target.chunk) ?: return
        event.isCancelled = !canDamage(land, attacker, target)
    }

    @EventHandler
    fun handleProjectileEntityHit(event: ProjectileHitEvent) {

        val entity = event.entity
        val shooter = if (entity.shooter is Entity) {
            entity.shooter as Entity
        } else null ?: return

        val hitEntity = event.hitEntity ?: return
        val land = pandorasClusterApi.getLand(hitEntity.chunk) ?: return

        event.isCancelled = !canDamage(land, shooter, hitEntity)
    }

    @EventHandler
    fun handleProjectileBlockHit(event: ProjectileHitEvent) {

        val entity = event.entity
        val shooter = if (entity.shooter is Entity) {
            entity.shooter as Entity
        } else null ?: return

        val hitBlock = event.hitBlock ?: return
        val land = pandorasClusterApi.getLand(hitBlock.chunk) ?: return
        event.isCancelled = !land.hasAccess(shooter.uniqueId)
    }

    private fun canDamage(land: Land, attacker: Entity, target: Entity): Boolean {

        if (target is Player && attacker is Player) {
            if (hasPermission(attacker, LandFlag.PVP)) return true
            if (land.hasAccess(attacker.uniqueId)) return true
            return land.getLandFlag(LandFlag.PVP).getValue<Boolean>() == true
        }

        val pveFlag = land.getLandFlag(LandFlag.PVE).getValue<Boolean>() == true

        val targetPlayer = targetPlayer(attacker, target)
        if (targetPlayer != null) {
            if (hasPermission(targetPlayer, LandFlag.PVE)) return true
            if (land.hasAccess(targetPlayer.uniqueId)) return true
            return pveFlag
        }

        return pveFlag
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