package net.onelitefeather.pandorascluster.dto.player;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LandPlayerDto extends PandorasModel {

    @Nullable
    Long id();

    @NotNull
    String uuid();

    @NotNull
    String name();
}
