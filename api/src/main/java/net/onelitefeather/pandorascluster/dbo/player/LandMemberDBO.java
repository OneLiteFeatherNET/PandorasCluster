package net.onelitefeather.pandorascluster.dbo.player;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.dbo.land.LandAreaDBO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LandMemberDBO extends PandorasModel {

    @Nullable
    Long id();

    @NotNull
    LandPlayerDBO member();

    @NotNull
    LandRole role();

    LandAreaDBO landArea();
}
