package net.onelitefeather.pandorascluster.api.land.flag

import net.onelitefeather.pandorascluster.api.enums.LandRole

enum class LandFlag(val displayName: String, val landRole: LandRole) {

    PVP("pvp", LandRole.MEMBER),
    PVE("pve", LandRole.MEMBER),
    REDSTONE("redstone", LandRole.VISITOR),
    POTION_SPLASH("potion-splash", LandRole.MEMBER),
    HANGING_BREAK("hanging-break", LandRole.MEMBER),
    HANGING_PLACE("hanging-place", LandRole.MEMBER),
    VEHICLE_USE("vehicle-use", LandRole.MEMBER),
    VEHICLE_CREATE("vehicle-create", LandRole.MEMBER),
    VEHICLE_DAMAGE("vehicle-damage", LandRole.MEMBER),
    LEAVES_DECAY("leaves-decay", LandRole.MEMBER),
    ENTITY_CHANGE_BLOCK("entity-change-block", LandRole.MEMBER),
    EXPLOSIONS("explosions", LandRole.MEMBER),
    MOB_GRIEFING("mob-griefing", LandRole.MEMBER),
    ICE_FORM("ice-form", LandRole.MEMBER),
    BLOCK_FORM("block-form", LandRole.MEMBER),
    TURTLE_EGG_DESTROY("turtle-egg-destroy", LandRole.MEMBER),
    UNKNOWN("unknown", LandRole.MEMBER),
    INTERACT_CROPS("interact-crops", LandRole.MEMBER),
    ENTITY_MOUNT("entity-mount", LandRole.MEMBER),
    ENTITY_TAME("entity-tame", LandRole.MEMBER),
    BUCKET_INTERACT("bucket-interact", LandRole.MEMBER),
    SHEAR_BLOCK("shear-block", LandRole.MEMBER),
    SHEAR_ENTITY("shear-entity", LandRole.MEMBER),
    TAKE_LECTERN("take-lectern", LandRole.MEMBER),
    ENTITY_LEASH("entity-leash", LandRole.MEMBER),
    USE_BED("use-bed", LandRole.MEMBER),
    VILLAGER_INTERACT("villager-interact", LandRole.MEMBER),
    FIRE_PROTECTION("fire-protection", LandRole.MEMBER),
    FARMLAND_DESTROY("farmland-destroy", LandRole.MEMBER),
    INTERACT_CONTAINERS("interact-containers", LandRole.MEMBER),
    SPONGE_ABSORB("sponge-absorb", LandRole.MEMBER),
    BLOCK_BREAK("block-break", LandRole.MEMBER),
    BLOCK_PLACE("block-place", LandRole.MEMBER),
    INTERACT_JUKEBOX("jukebox-interact", LandRole.MEMBER);

    companion object {
        val LAND_FLAGS = LandFlag.entries.toTypedArray()

        fun findByName(name: String): LandFlag? = LAND_FLAGS.firstOrNull { filterFlagName(it, name) }
        fun getDefaultFlagNames(): List<String> = LAND_FLAGS.map { it.name }

        fun filterFlagName(landFlag: LandFlag, flagName: String) =
            landFlag.name == flagName.uppercase() ||
                    landFlag.displayName.equals(flagName, true)
    }
}

