package net.onelitefeather.pandorascluster.dbo.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LandPlayerDBO {

    @Nullable
    Long id();

    @NotNull
    String uuid();

    @NotNull
    String name();
}
