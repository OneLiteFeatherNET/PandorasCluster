package net.onelitefeather.pandorascluster.dto.position;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HomePositionDto extends PandorasModel {

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
