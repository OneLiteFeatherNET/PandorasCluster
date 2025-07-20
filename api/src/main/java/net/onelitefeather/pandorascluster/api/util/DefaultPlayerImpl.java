package net.onelitefeather.pandorascluster.api.util;

import net.onelitefeather.pandorascluster.api.enums.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class DefaultPlayerImpl implements PlayerUtil {
    @Override
    public @Nullable Object getPlayer(@NotNull UUID var1) {
        throw new IllegalStateException("This is not implemented yet!");
    }

    @Override
    public boolean isOnline(@NotNull UUID var1) {
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull UUID var1, @NotNull String var2) {
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull UUID var1, @NotNull Permission var2) {
        return false;
    }
}
