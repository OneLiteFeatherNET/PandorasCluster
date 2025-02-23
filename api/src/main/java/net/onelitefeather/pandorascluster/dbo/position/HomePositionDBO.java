package net.onelitefeather.pandorascluster.dbo.position;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HomePositionDBO {

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
