package net.onelitefeather.pandorascluster.dto.flag;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EntityCapFlagDto extends PandorasModel {

    @Nullable
    Long id();

    @NotNull
    String name();

    Integer spawnLimit();

    FlagContainerDto flagContainer();
}
