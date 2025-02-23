package net.onelitefeather.pandorascluster.util;

import net.onelitefeather.pandorascluster.api.enums.Permission;
import net.onelitefeather.pandorascluster.api.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BukkitPlayerUtil implements PlayerUtil {

    @Override
    public @Nullable Player getPlayer(@NotNull UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public boolean hasPermission(@NotNull UUID uuid, @NotNull String permission) {
        var player = getPlayer(uuid);
        if (player == null) return false;
        return player.hasPermission(permission);
    }

    @Override
    public boolean hasPermission(@NotNull UUID uuid, @NotNull Permission permission) {
        return hasPermission(uuid, permission.getPermissionNode());
    }
}
