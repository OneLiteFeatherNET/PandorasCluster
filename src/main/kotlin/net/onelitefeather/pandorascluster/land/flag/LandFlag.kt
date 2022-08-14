package net.onelitefeather.pandorascluster.land.flag

/**
 * case 0 -> this.value;
 * case 1 -> Integer.getInteger(this.value);
 * case 2 -> Boolean.getBoolean(this.value);
 * case 3 -> Double.parseDouble(this.value);
 * case 4 -> Float.parseFloat(this.value);
 */
enum class LandFlag(val flagName: String,
                    val defaultValue: Any,
                    val landFlagType: LandFlagType,
                    val type: Byte) {

    PVP("pvp", false, LandFlagType.PLAYER, 2),
    PVE("pve", false, LandFlagType.PLAYER, 2),
    REDSTONE("redstone", true, LandFlagType.WORLD_TICK, 2),
    POTION_SPLASH("potion-splash", false, LandFlagType.ENTITY, 2),
    HANGING_BREAK("hanging-break", false, LandFlagType.ENTITY, 2),

    VEHICLE_USE("vehicle-use", false, LandFlagType.ENTITY, 2),
    VEHICLE_CREATE("vehicle-create", false, LandFlagType.ENTITY, 2),
    VEHICLE_DAMAGE("vehicle-damage", false, LandFlagType.ENTITY, 2),

    INTERACT_CONTAINERS("interact-containers", false, LandFlagType.PLAYER, 2),
    LEAVES_DECAY("leaves-decay", false, LandFlagType.WORLD_TICK, 2),

    EXPLOSIONS("explosions", true, LandFlagType.EXPLOSION, 2),
    FARMLAND_DESTROY("farmland-destroy", true, LandFlagType.ENTITY, 2),
    MOB_GRIEFING("mob-griefing", true, LandFlagType.ENTITY, 2),
    ICE_FORM("ice-form", false, LandFlagType.WORLD_TICK, 2),
    BLOCK_FORM("block-form", false, LandFlagType.ENTITY, 2);
}

val LAND_FLAGS = LandFlag.values()
fun findByName(name: String): LandFlag? =
    LAND_FLAGS.firstOrNull { landFlag -> landFlag.name == name.uppercase()  }