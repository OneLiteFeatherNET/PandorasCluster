package net.onelitefeather.pandorascluster.dbo.flag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NaturalFlagDBO {

    @Nullable
    Long id();

    @NotNull
    String name();

    boolean state();
}
