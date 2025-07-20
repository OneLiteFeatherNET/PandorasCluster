package net.onelitefeather.pandorascluster.api.util;

import net.kyori.adventure.util.Services;
import net.onelitefeather.pandorascluster.api.enums.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface PlayerUtil {

    @Nullable
    Object getPlayer(@NotNull UUID uuid);

    default boolean isOnline(@NotNull UUID uuid) {
        return getPlayer(uuid) != null;
    }

    boolean hasPermission(@NotNull UUID uuid, @NotNull String permission);

    boolean hasPermission(@NotNull UUID uuid, @NotNull Permission permission);

    final class Instances {

        @NotNull
        public static final PlayerUtil instance;

        static {
            Optional<PlayerUtil> service = Services.service(PlayerUtil.class);
            instance = service.orElseGet(DefaultPlayerImpl::new);
        }
    }
}
