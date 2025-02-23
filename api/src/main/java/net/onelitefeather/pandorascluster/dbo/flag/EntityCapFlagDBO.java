package net.onelitefeather.pandorascluster.dbo.flag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EntityCapFlagDBO {

    @Nullable
    Long id();

    @NotNull
    String name();

    Integer spawnLimit();

    FlagContainerDBO flagContainer();
}
