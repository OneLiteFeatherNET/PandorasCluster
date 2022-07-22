package net.onelitefeather.pandorascluster.enums;

import net.onelitefeather.pandorascluster.util.Constants;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

public enum Permission {

    BLOCK_BREAK("%s.admin.block.break"),
    BLOCK_PLACE("%s.admin.block.place"),
    ENTITY_MOUNT("%s.admin.mount"),
    LAND_ENTRY_DENIED("%s.admin.entry.denied"),

    VEHICLE_ENTER("%s.admin.vehicle.use"),
    VEHICLE_DAMAGE("%s.admin.vehicle.damage"),
    VEHICLE_DESTROY("%s.admin.vehicle.destroy"),
    EXPLOSION("%s.admin.explosion"),
    USE_REDSTONE("%s.admin.redstone"),
    INTERACT_CONTAINERS("%s.admin.interact.container"),
    INTERACT_FARMLAND("%s.admin.interact.farmland"),
    POTION_SPLASH("%s.admin.potion.splash"),
    PROJECTILE_HIT_ENTITY("%s.admin.projectile.hit");

    private final String permission;

    Permission(@NotNull String permission) {
        this.permission = permission;
    }

    @NotNull
    public String getPermission() {
        return String.format(permission, Constants.PLUGIN_NAME);
    }

    public boolean hasPermission(@NotNull Permissible permissible) {
       return permissible.hasPermission(getPermission());
    }
}
