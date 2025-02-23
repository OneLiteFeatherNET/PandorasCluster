package net.onelitefeather.pandorascluster.dbo.flag;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NaturalFlagDBO extends PandorasModel {

    @Nullable
    Long id();

    @NotNull
    String name();

    boolean state();

    FlagContainerDBO flagContainer();
}
