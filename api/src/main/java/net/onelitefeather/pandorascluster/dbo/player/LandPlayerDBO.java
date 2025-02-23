package net.onelitefeather.pandorascluster.dbo.player;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LandPlayerDBO extends PandorasModel {

    @Nullable
    Long id();

    @NotNull
    String uuid();

    @NotNull
    String name();
}
