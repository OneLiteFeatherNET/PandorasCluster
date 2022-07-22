package net.onelitefeather.pandorascluster.land.flag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * case 0 -> this.value;
 * case 1 -> Integer.getInteger(this.value);
 * case 2 -> Boolean.getBoolean(this.value);
 * case 3 -> Double.parseDouble(this.value);
 * case 4 -> Float.parseFloat(this.value);
 */
public enum LandFlag {

    PVP("pvp", false, LandFlagType.PLAYER, (byte) 2),
    PVE("pve", false, LandFlagType.PLAYER, (byte) 2),
    REDSTONE("redstone", true, LandFlagType.WORLD_TICK, (byte) 2),
    POTION_SPLASH("potion-splash", false, LandFlagType.ENTITY, (byte) 2),

    HANGING_BREAK("hanging-break", false, LandFlagType.ENTITY, (byte) 2),


    VEHICLE_USE("vehicle-use", false, LandFlagType.ENTITY, (byte) 2),
    VEHICLE_CREATE("vehicle-create", false, LandFlagType.ENTITY, (byte) 2),
    VEHICLE_DAMAGE("vehicle-create", false, LandFlagType.ENTITY, (byte) 2),

    INTERACT_CONTAINERS("interact-containers", false, LandFlagType.PLAYER, (byte) 2),
    LEAVES_DECAY("leaves-decay", false, LandFlagType.WORLD_TICK, (byte) 2),
    EXPLOSIONS("explosions", true, LandFlagType.EXPLOSION, (byte) 2),
    FARMLAND_DESTROY("farmland-destroy", true, LandFlagType.ENTITY, (byte) 2),
    MOB_GRIEFING("mob-griefing", true, LandFlagType.ENTITY, (byte) 2),
    ICE_FORM("ice-form", false, LandFlagType.WORLD_TICK, (byte) 2),
    BLOCK_FORM("block-form", false, LandFlagType.ENTITY, (byte) 2);

    private final String name;
    private final Object defaultValue;
    private final LandFlagType flagType;
    private final byte type;

    private final static HashMap<String, LandFlag> FLAG_HASHMAP = new HashMap<>();

    LandFlag(@NotNull String name, @NotNull Object defaultValue, @NotNull LandFlagType flagType, byte type) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.flagType = flagType;
        this.type = type;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Object getDefaultValue() {
        return defaultValue;
    }

    public byte getType() {
        return type;
    }

    @NotNull
    public LandFlagType getFlagType() {
        return flagType;
    }

    @Nullable
    public static LandFlag findByName(@NotNull String name) {
        return FLAG_HASHMAP.getOrDefault(name, null);
    }

    public static HashMap<String, LandFlag> getFlagHashmap() {
        return FLAG_HASHMAP;
    }

    static {
        for (LandFlag value : LandFlag.values()) {
            FLAG_HASHMAP.put(value.name, value);
        }
    }
}
