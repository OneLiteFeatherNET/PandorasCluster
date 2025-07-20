package net.onelitefeather.pandorascluster.dto.flag;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NaturalFlagDto extends PandorasModel {

    @Nullable
    Long id();

    @NotNull
    String name();

    boolean state();

    FlagContainerDto flagContainer();
}
