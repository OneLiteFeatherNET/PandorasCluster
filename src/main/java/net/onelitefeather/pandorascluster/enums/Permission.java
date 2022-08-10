package net.onelitefeather.pandorascluster.enums;

import net.onelitefeather.pandorascluster.util.Constants;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

public enum Permission {

    BLOCK_BREAK("%s.unowned.block.break"),
    BLOCK_PLACE("%s.unowned.block.place"),
    ENTITY_MOUNT("%s.unowned.mount"),
    LAND_ENTRY_DENIED("%s.unowned.entry.denied"),

    PVP("%s.unowned.attack.player"),
    PVE("%s.unowned.attack.entity"),

    VEHICLE_ENTER("%s.unowned.vehicle.use"),
    VEHICLE_DAMAGE("%s.unowned.vehicle.damage"),
    VEHICLE_DESTROY("%s.unowned.vehicle.destroy"),
    EXPLOSION("%s.unowned.explosion"),
    INTERACT_CONTAINERS("%s.unowned.interact.container"),
    INTERACT_FARMLAND("%s.unowned.interact.farmland"),
    INTERACT_USE("%s.unowned.interact.use"),
    POTION_SPLASH("%s.unowned.potion.splash"),
    PROJECTILE_HIT_ENTITY("%s.unowned.projectile.hit");

    private final String permissionNode;

    Permission(@NotNull String permissionNode) {
        this.permissionNode = permissionNode;
    }

    @NotNull
    public String getPermissionNode() {
        return String.format(permissionNode, Constants.PLUGIN_NAME);
    }

    public boolean hasPermission(@NotNull Permissible permissible) {
       return permissible.hasPermission(getPermissionNode());
    }
}
