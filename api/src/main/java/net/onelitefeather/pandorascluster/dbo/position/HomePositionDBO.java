package net.onelitefeather.pandorascluster.dbo.position;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HomePositionDBO extends PandorasModel {

    @Nullable
    Long id();

    @NotNull
    String world();

    double posX();

    double posY();

    double posZ();

    float yaw();

    float pitch();
}
