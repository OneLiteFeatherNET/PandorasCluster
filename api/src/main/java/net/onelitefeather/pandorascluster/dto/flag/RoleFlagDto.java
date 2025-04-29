package net.onelitefeather.pandorascluster.dto.flag;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RoleFlagDto extends PandorasModel {

    @Nullable
    Long id();

    @NotNull
    String name();

    boolean state();

    @NotNull
    LandRole role();

    FlagContainerDto flagContainer();
}
