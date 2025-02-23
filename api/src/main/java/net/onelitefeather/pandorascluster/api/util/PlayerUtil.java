package net.onelitefeather.pandorascluster.api.util;

import net.kyori.adventure.util.Services;
import net.onelitefeather.pandorascluster.api.enums.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface PlayerUtil {

    @Nullable
    Object getPlayer(@NotNull UUID var1);

    boolean isOnline(@NotNull UUID var1);

    boolean hasPermission(@NotNull UUID var1, @NotNull String var2);

    boolean hasPermission(@NotNull UUID var1, @NotNull Permission var2);

    final class Instances {

        @NotNull
        public static final PlayerUtil instance;

        static {
            Optional<PlayerUtil> service = Services.service(PlayerUtil.class);
            instance = service.orElseGet(DefaultPlayerImpl::new);
        }
    }
}
