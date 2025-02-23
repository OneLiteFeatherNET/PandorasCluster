package net.onelitefeather.pandorascluster.dbo.flag;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.dbo.land.LandAreaDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandDBO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RoleFlagDBO {

    @Nullable
    Long id();

    @NotNull
    String name();

    boolean state();

    @NotNull
    LandRole role();

    FlagContainerDBO flagContainer();
}
